package top.inept.blog.feature.auth.service.impl

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.AuthErrorCode
import top.inept.blog.feature.auth.constant.JwtClaimConstants
import top.inept.blog.feature.auth.model.dto.AuthLoginDTO
import top.inept.blog.feature.auth.model.dto.LoginResult
import top.inept.blog.feature.auth.model.dto.RefreshResult
import top.inept.blog.feature.auth.model.entity.RefreshToken
import top.inept.blog.feature.auth.model.vo.AuthLoginVO
import top.inept.blog.feature.auth.repository.RefreshTokenRepository
import top.inept.blog.feature.auth.service.AuthService
import top.inept.blog.feature.user.repository.UserRepository
import top.inept.blog.properties.JwtProperties
import top.inept.blog.utils.PasswordUtil
import top.inept.blog.utils.ShaUtil
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties,
    @Qualifier("accessJwtEncoder") private val accessEncoder: JwtEncoder,
    @Qualifier("refreshJwtEncoder") private val refreshEncoder: JwtEncoder,
    @Qualifier("refreshJwtDecoder") private val refreshDecoder: JwtDecoder,
) : AuthService {
    private fun getRefreshToken(refreshTokenString: String): RefreshToken {
        val refreshTokenSha256 = ShaUtil.sha256Hex(refreshTokenString)
        return refreshTokenRepository.findByTokenHash(refreshTokenSha256)
            ?: throw BusinessException(AuthErrorCode.REFRESH_TOKEN_DB_NOT_FOUND)
    }

    private fun getRefreshTokenForUpdate(refreshTokenString: String): RefreshToken {
        val refreshTokenSha256 = ShaUtil.sha256Hex(refreshTokenString)
        return refreshTokenRepository.findForUpdateByTokenHash(refreshTokenSha256)
            ?: throw BusinessException(AuthErrorCode.REFRESH_TOKEN_DB_NOT_FOUND)
    }

    @Transactional
    override fun login(dto: AuthLoginDTO): LoginResult {
        //根据用户名查找用户
        val user = userRepository.findByUsername(dto.username)
            ?: throw BusinessException(AuthErrorCode.USERNAME_OR_PASSWORD)

        //判断用户的状态
        if (!user.status) {
            throw BusinessException(AuthErrorCode.DISABLE_USER, user.username)
        }

        //校验密码
        if (!PasswordUtil.matches(dto.password, user.password))
            throw BusinessException(AuthErrorCode.USERNAME_OR_PASSWORD)

        //生成RefreshToken
        val refreshJwt = createRefreshJwt(user.username)
        val refreshToken = refreshJwt.tokenValue

        //保存RefreshToken
        val dbRefreshToken = RefreshToken(
            user = user,
            tokenHash = ShaUtil.sha256Hex(refreshToken),
            expiresAt = refreshJwt.expiresAt!!,
            createdAt = refreshJwt.issuedAt!!,
            familyId = UUID.randomUUID()
        )
        refreshTokenRepository.saveAndFlush(dbRefreshToken)

        //签发AccessToken
        val permissionCodes = userRepository.findPermissionCodes(user.id)
        val accessJwt = createAccessJwt(permissionCodes, user.id, user.username)

        val vo = AuthLoginVO(
            id = user.id,
            nickname = user.nickname,
            username = user.username,
            email = user.email,
            accessToken = accessJwt.tokenValue,
        )

        return LoginResult(vo, refreshToken)
    }

    @Transactional
    override fun refreshToken(refreshTokenString: String): RefreshResult {
        if (refreshTokenString.isEmpty()) throw BusinessException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND)

        val now = Instant.now()

        //校验refreshToken是否合法
        val jwt = try {
            refreshDecoder.decode(refreshTokenString)
        } catch (_: JwtException) {
            throw BusinessException(AuthErrorCode.TOKEN_VERIFICATION)
        }

        //根据sha256去数据库查找RefreshToken
        val oldDbRefreshToken = getRefreshTokenForUpdate(refreshTokenString)

        val user = oldDbRefreshToken.user

        //判断RefreshToken是否已经被使用过
        if (oldDbRefreshToken.lastUsedAt != null) {
            //使用过被重复刷新可能代表RefreshToken被盗用，将同一个replacedById的RefreshToken全部撤销，让用户重新登录
            refreshTokenRepository.revokeTokenByFamilyId(oldDbRefreshToken.familyId, now)
            throw BusinessException(AuthErrorCode.REFRESH_TOKEN_REUSED)
        }
        //判断用户状态
        if (!user.status) throw BusinessException(AuthErrorCode.DISABLE_USER, user.username)
        //是否被撤销
        if (oldDbRefreshToken.revokedAt != null) throw BusinessException(AuthErrorCode.TOKEN_HAS_BEEN_REVOKED)
        //是否过期
        if (oldDbRefreshToken.expiresAt.isBefore(now)) throw BusinessException(AuthErrorCode.TOKEN_EXPIRED)

        //校验token的使用类型
        val tokenUse = jwt.claims[JwtClaimConstants.TOKEN_USE] as? String
        if (tokenUse != JwtClaimConstants.TOKEN_USE_REFRESH) {
            throw BusinessException(
                AuthErrorCode.TOKEN_USE_TYPE_VERIFICATION,
                JwtClaimConstants.TOKEN_USE_REFRESH,
                tokenUse ?: "null"
            )
        }

        //签发AccessToken
        val permissionCodes = userRepository.findPermissionCodes(user.id)
        val accessJwt = createAccessJwt(permissionCodes, user.id, user.username)

        //签发新的RefreshToken
        val newRefreshJwt = createRefreshJwt(user.username)
        val newRefreshToken = newRefreshJwt.tokenValue

        //记录旧的RefreshToken使用情况
        oldDbRefreshToken.lastUsedAt = now
        refreshTokenRepository.save(oldDbRefreshToken)

        //保存新签发的RefreshToken
        val newDbRefreshToken = RefreshToken(
            user = user,
            tokenHash = ShaUtil.sha256Hex(newRefreshToken),
            expiresAt = newRefreshJwt.expiresAt!!,
            createdAt = newRefreshJwt.issuedAt!!,
            familyId = oldDbRefreshToken.familyId
        )
        refreshTokenRepository.saveAndFlush(newDbRefreshToken)

        return RefreshResult(accessJwt.tokenValue, newRefreshToken)
    }

    @Transactional
    override fun logout(refreshTokenString: String) {
        if (refreshTokenString.isEmpty()) throw BusinessException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND)

        //根据sha256去数据库查找RefreshToken
        val dbRefreshToken = getRefreshTokenForUpdate(refreshTokenString)

        //校验RefreshToken是否合法
        val jwt = try {
            refreshDecoder.decode(refreshTokenString)
        } catch (_: JwtException) {
            throw BusinessException(AuthErrorCode.TOKEN_VERIFICATION)
        }

        //是否已经撤销
        if (dbRefreshToken.revokedAt != null) throw BusinessException(AuthErrorCode.TOKEN_HAS_BEEN_REVOKED)

        //撤销信息写入数据库
        dbRefreshToken.revokedAt = Instant.now()
        refreshTokenRepository.saveAndFlush(dbRefreshToken)
    }

    private fun createRefreshJwt(username: String): Jwt {
        //现在的时间
        val now = Instant.now()
        //过期时间
        val exp = now.plus(Duration.ofMinutes(jwtProperties.refreshExpiresMinutes))

        val claims = JwtClaimsSet.builder().apply {
            id(UUID.randomUUID().toString())
            issuer(jwtProperties.issuer)
            issuedAt(now)
            expiresAt(exp)
            subject(username)
            claim(JwtClaimConstants.TOKEN_USE, JwtClaimConstants.TOKEN_USE_REFRESH)
        }.build()

        val headers = JwsHeader.with(MacAlgorithm.HS256).build()
        return refreshEncoder.encode(JwtEncoderParameters.from(headers, claims))
    }

    private fun createAccessJwt(permissionCodes: List<String>, userId: Long, username: String): Jwt {
        val authorities = permissionCodes.map { SimpleGrantedAuthority(it) }
        val auth = UsernamePasswordAuthenticationToken(username, null, authorities)

        return createAccessJwt(auth, userId)
    }

    private fun createAccessJwt(auth: Authentication, userId: Long): Jwt {
        //现在的时间
        val now = Instant.now()
        //过期时间
        val exp = now.plus(Duration.ofMinutes(jwtProperties.accessExpiresMinutes))
        //权限
        val authorities = auth.authorities.map { it.authority }

        val claims = JwtClaimsSet.builder().apply {
            issuer(jwtProperties.issuer)
            issuedAt(now)
            expiresAt(exp)
            subject(auth.name)
            claim(JwtClaimConstants.TOKEN_USE, JwtClaimConstants.TOKEN_USE_ACCESS)
            claim(JwtClaimConstants.USER_ID, userId.toString())
            claim(JwtClaimConstants.AUTH, authorities)
        }.build()

        val headers = JwsHeader.with(MacAlgorithm.HS256).build()
        return accessEncoder.encode(JwtEncoderParameters.from(headers, claims))
    }
}