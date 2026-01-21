package top.inept.blog.feature.auth.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import top.inept.blog.exception.NotFoundException
import top.inept.blog.extensions.get
import top.inept.blog.feature.auth.model.LoginBundle
import top.inept.blog.feature.auth.model.dto.AuthLoginDTO
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

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties,
    private val messages: MessageSourceAccessor,
    @Qualifier("accessJwtEncoder") private val accessEncoder: JwtEncoder,
    @Qualifier("refreshJwtEncoder") private val refreshEncoder: JwtEncoder,
    @Qualifier("accessJwtDecoder") private val accessDecoder: JwtDecoder,
    @Qualifier("refreshJwtDecoder") private val refreshDecoder: JwtDecoder,
) : AuthService {
    /**
     * Login
     *
     * @param dto
     * @return AuthLoginVO,refreshToken
     */
    override fun login(dto: AuthLoginDTO): LoginBundle {
        //根据用户名查找用户
        val dbUser = userRepository.findByUsername(dto.username)

        //没有用户
        if (dbUser == null) throw NotFoundException(messages["message.user.user_not_found"])

        //校验密码
        if (!PasswordUtil.matches(dto.password, dbUser.password))
            throw Exception(messages["message.user.username_or_password_error"])

        //生成refreshToken
        val jwt = createRefreshToken(dbUser.username)
        val refreshToken = jwt.tokenValue

        //保存refreshToken
        //TODO 最好查查有没有 replacedBy
        val dbRefreshToken = RefreshToken(
            user = dbUser,
            tokenHash = ShaUtil.sha256Hex(refreshToken),
            expiresAt = jwt.expiresAt!!,
            createdAt = jwt.issuedAt!!,
        )
        refreshTokenRepository.save(dbRefreshToken)

        //生成accessToken
        val accessToken = refresh(refreshToken)

        val vo = AuthLoginVO(
            id = dbUser.id,
            nickname = dbUser.nickname,
            username = dbUser.username,
            email = dbUser.email,
            accessToken = accessToken
        )

        return LoginBundle(vo, refreshToken)
    }

    /**
     * Refresh
     *
     * @param refreshTokenString
     * @return accessToken
     */
    override fun refresh(refreshTokenString: String): String {
        val now = Instant.now()

        //根据sha256去数据库查找refreshToken
        val refreshTokenSha256 = ShaUtil.sha256Hex(refreshTokenString)
        val dbRefreshToken = refreshTokenRepository.findByTokenHash(refreshTokenSha256) ?: throw NotFoundException(
            messages["message.auth.refresh_token_not_found"]
        )

        //是否被撤销
        if (dbRefreshToken.revokedAt != null) {
            throw Exception(messages["message.auth.token_has_been_revoked"])
        }
        //是否过期
        if (dbRefreshToken.expiresAt.isBefore(now)) {
            throw Exception(messages["message.auth.token_expired"])
        }

        //校验refreshToken是否合法
        val jwt =
            try {
                refreshDecoder.decode(refreshTokenString)
            } catch (e: JwtException) {
                throw Exception(messages["message.auth.unknown_token_content_error"])
            }

        //校验jwt的subject内容
        if (jwt.subject != dbRefreshToken.user.username)
            throw Exception(messages["message.auth.unknown_token_content_error"])

        //校验token的使用类型
        val tokenUse = jwt.claims["token_use"] as? String
        if (tokenUse != "refresh") {
            throw Exception(messages["message.auth.unknown_token_content_error"])
        }

        //构建用户拥有的权限
        val permissionCodes = userRepository.findPermissionCodes(dbRefreshToken.user.id)
        val authorities = permissionCodes.map { SimpleGrantedAuthority(it) }
        val auth =
            UsernamePasswordAuthenticationToken(dbRefreshToken.user.username, null, authorities)

        //构建token
        val token = createAccessToken(auth)

        //记录使用情况
        dbRefreshToken.lastUsedAt = Instant.now()
        refreshTokenRepository.saveAndFlush(dbRefreshToken)

        return token
    }

    override fun logout(token: String) {
        //根据sha256去数据库查找refreshToken
        val refreshTokenSha256 = ShaUtil.sha256Hex(token)
        val dbRefreshToken = refreshTokenRepository.findByTokenHash(refreshTokenSha256) ?: throw NotFoundException(
            messages["message.auth.refresh_token_not_found"]
        )

        //校验refreshToken是否合法
        val jwt =
            try {
                refreshDecoder.decode(token)
            } catch (e: JwtException) {
                throw Exception(messages["message.auth.unknown_token_content_error"])
            }

        //校验token的使用类型
        val tokenUse = jwt.claims["token_use"] as? String
        if (tokenUse != "refresh") {
            throw Exception(messages["message.auth.unknown_token_content_error"])
        }

        //撤销写入数据库
        dbRefreshToken.revokedAt = Instant.now()
        refreshTokenRepository.saveAndFlush(dbRefreshToken)
    }

    private fun createRefreshToken(username: String): Jwt {
        //现在的时间
        val now = Instant.now()
        //过期时间
        val exp = now.plus(Duration.ofMinutes(jwtProperties.refreshExpiresMinutes))

        val claims = JwtClaimsSet.builder().apply {
            issuer(jwtProperties.issuer)
            issuedAt(now)
            expiresAt(exp)
            subject(username)
            claim("token_use", "refresh")
        }.build()

        val headers = JwsHeader.with(MacAlgorithm.HS256).build()
        return refreshEncoder.encode(JwtEncoderParameters.from(headers, claims))
    }

    private fun createAccessToken(auth: Authentication): String {
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
            claim("auth", authorities)
        }.build()

        val headers = JwsHeader.with(MacAlgorithm.HS256).build()
        return accessEncoder.encode(JwtEncoderParameters.from(headers, claims)).tokenValue
    }
}