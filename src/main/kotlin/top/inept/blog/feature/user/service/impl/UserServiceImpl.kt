package top.inept.blog.feature.user.service.impl

import org.slf4j.LoggerFactory
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import top.inept.blog.base.QueryBuilder
import top.inept.blog.exception.NotFoundException
import top.inept.blog.extensions.get
import top.inept.blog.extensions.toPageRequest
import top.inept.blog.feature.user.model.dto.*
import top.inept.blog.feature.user.model.entity.User
import top.inept.blog.feature.user.model.vo.LoginUserVO
import top.inept.blog.feature.user.repository.UserRepository
import top.inept.blog.feature.user.repository.UserSpecs
import top.inept.blog.feature.user.service.UserService
import top.inept.blog.properties.JwtProperties
import top.inept.blog.utils.PasswordUtil
import top.inept.blog.utils.SecurityUtil
import java.time.Duration
import java.time.Instant

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val jwtProperties: JwtProperties,
    private val messages: MessageSourceAccessor,
    private val encoder: JwtEncoder,
) : UserService {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun getUsers(queryUserDTO: QueryUserDTO): Page<User> {
        val pageRequest = queryUserDTO.toPageRequest()

        val specs = QueryBuilder<User>()
            .or(
                UserSpecs.nicknameContains(queryUserDTO.keyword),
                UserSpecs.usernameContains(queryUserDTO.keyword),
                UserSpecs.emailContains(queryUserDTO.keyword)
            ).buildSpec()

        return userRepository.findAll(specs, pageRequest)
    }

    override fun getUserById(id: Long): User {
        //根据id查找用户
        return userRepository.findByIdOrNull(id) ?: throw NotFoundException("message.user.user_not_found")
    }

    override fun getUserByUsername(username: String): User {
        //根据username查找用户
        return userRepository.findByUsername(username) ?: throw NotFoundException("message.user.user_not_found")
    }

    override fun createUser(createUserDTO: CreateUserDTO): User {
        //判断用户字段是否重复
        validateUniqueUserFields(
            username = createUserDTO.username,
            nickname = createUserDTO.nickname,
            email = createUserDTO.email,
        )

        val encodePassword =
            PasswordUtil.encode(createUserDTO.password) ?: throw Exception(messages["message.common.unknown_error"])

        val dbUser = User(
            username = createUserDTO.username,
            nickname = createUserDTO.nickname,
            email = createUserDTO.email,
            password = encodePassword
        )

        return userRepository.save(dbUser)
    }

    override fun updateUser(updateUserDTO: UpdateUserDTO): User {
        //根据id查找用户
        val dbUser = userRepository.findByIdOrNull(updateUserDTO.id)
            ?: throw Exception(messages["message.user.user_not_found"])

        //判断用户字段是否重复
        validateUniqueUserFields(
            username = if (updateUserDTO.username != dbUser.username) updateUserDTO.username else null,
            nickname = if (updateUserDTO.nickname != dbUser.nickname) updateUserDTO.nickname else null,
            email = if (updateUserDTO.email != dbUser.email) updateUserDTO.email else null,
        )

        dbUser.apply {
            nickname = updateUserDTO.nickname
            username = updateUserDTO.username
            email = updateUserDTO.email

            updateUserDTO.password?.let {
                PasswordUtil.encode(it)?.let { encodePassword ->
                    password = encodePassword
                }
            }
        }

        return userRepository.save(dbUser)
    }

    override fun deleteUserById(id: Long) {
        //根据id判断用户是否存在
        if (!userRepository.existsById(id)) throw NotFoundException(messages["message.user.user_not_found"])

        //删除用户
        userRepository.deleteById(id)
    }

    override fun loginUser(userLoginDTO: LoginUserDTO): LoginUserVO {
        //根据用户名查找用户
        val dbUser = userRepository.findByUsername(userLoginDTO.username)

        //没有用户
        if (dbUser == null) throw NotFoundException(messages["message.user.user_not_found"])

        //校验密码
        if (!PasswordUtil.matches(userLoginDTO.password, dbUser.password))
            throw Exception(messages["message.user.username_or_password_error"])

        //构建用户拥有的权限
        val permissionCodes = userRepository.findPermissionCodes(dbUser.id)
        val authorities = permissionCodes.map { SimpleGrantedAuthority(it) }
        val auth =
            UsernamePasswordAuthenticationToken(dbUser.username, null, authorities)

        //生成token
        val token = crateToken(auth)

        return LoginUserVO(
            id = dbUser.id,
            nickname = dbUser.nickname,
            username = dbUser.username,
            email = dbUser.email,
            token = token
        )
    }

    private fun crateToken(auth: Authentication): String {
        //现在的时间
        val now = Instant.now()
        //过期时间
        val exp = now.plus(Duration.ofMinutes(jwtProperties.expiresMinutes))
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
        return encoder.encode(JwtEncoderParameters.from(headers, claims)).tokenValue
    }

    override fun getProfile(): User {
        //从上下文获取用户名
        val contextUsername = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw Exception("message.common.missing_user_context")

        //根据用户名获取用户
        return getUserByUsername(contextUsername)
    }

    override fun updateProfile(updateUserProfileDTO: UpdateUserProfileDTO): User {
        //从上下文获取用户名
        val contextUsername = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw Exception("message.common.missing_user_context")

        //根据用户名获取用户
        val user = getUserByUsername(contextUsername)

        //判断用户字段是否重复
        validateUniqueUserFields(
            nickname = if (updateUserProfileDTO.nickname != user.nickname) updateUserProfileDTO.nickname else null,
        )

        user.apply {
            nickname = updateUserProfileDTO.nickname
            updateUserProfileDTO.password?.let {
                PasswordUtil.encode(it)?.let { encodePassword ->
                    password = encodePassword
                }
            }
        }

        return userRepository.save(user)
    }

    /**
     * 验证唯一用户字段
     *
     * @param username
     * @param nickname
     * @param email
     */
    private fun validateUniqueUserFields(username: String? = null, nickname: String? = null, email: String? = null) {
        //判断有没有重复用户名
        if (username != null) if (userRepository.existsByUsername(username)) throw Exception(messages["message.user.duplicate_username"])

        //判断有没有重复昵称
        if (nickname != null) if (userRepository.existsByNickname(nickname)) throw Exception(messages["message.user.duplicate_nickname"])

        //判断有没有重复邮箱
        if (email != null) if (userRepository.existsByEmail(email)) throw Exception(messages["message.user.duplicate_email"])
    }
}