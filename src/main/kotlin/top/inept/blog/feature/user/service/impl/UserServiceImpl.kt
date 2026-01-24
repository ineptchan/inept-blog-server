package top.inept.blog.feature.user.service.impl

import com.querydsl.core.BooleanBuilder
import org.hibernate.exception.ConstraintViolationException
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import top.inept.blog.exception.DbDuplicateException
import top.inept.blog.exception.NotFoundException
import top.inept.blog.extensions.get
import top.inept.blog.extensions.toPageRequest
import top.inept.blog.feature.auth.repository.RefreshTokenRepository
import top.inept.blog.feature.user.model.dto.CreateUserDTO
import top.inept.blog.feature.user.model.dto.QueryUserDTO
import top.inept.blog.feature.user.model.dto.UpdateUserDTO
import top.inept.blog.feature.user.model.dto.UpdateUserProfileDTO
import top.inept.blog.feature.user.model.entity.QUser
import top.inept.blog.feature.user.model.entity.User
import top.inept.blog.feature.user.model.entity.constraints.UserConstraints
import top.inept.blog.feature.user.repository.UserRepository
import top.inept.blog.feature.user.service.UserService
import top.inept.blog.utils.PasswordUtil
import top.inept.blog.utils.SecurityUtil
import java.time.Instant

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val messages: MessageSourceAccessor,
    private val refreshRepository: RefreshTokenRepository
) : UserService {
    override fun getUsers(queryUserDTO: QueryUserDTO): Page<User> {
        val pageRequest = queryUserDTO.toPageRequest()
        val u = QUser.user

        val builder = BooleanBuilder().apply {
            queryUserDTO.keyword?.takeIf { it.isNotBlank() }?.let { kw ->
                and(
                    u.nickname.contains(kw)
                        .or(u.password.contains(kw))
                        .or(u.email.contains(kw))
                )
            }
        }

        return userRepository.findAll(builder, pageRequest)
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
        val encodePassword =
            PasswordUtil.encode(createUserDTO.password) ?: throw Exception(messages["message.common.unknown_error"])

        val dbUser = User(
            username = createUserDTO.username,
            nickname = createUserDTO.nickname,
            email = createUserDTO.email,
            password = encodePassword
        )

        //TODO 推荐不要传入密码，系统随机生成发邮件通知

        saveAndFlushUserOrThrow(dbUser)

        return dbUser
    }

    @Transactional
    override fun updateUser(id: Long, dto: UpdateUserDTO): User {
        //根据id查找用户
        val dbUser = userRepository.findByIdOrNull(id) ?: throw Exception(messages["message.user.user_not_found"])

        dbUser.apply {
            dto.nickname?.let { nickname = it }
            dto.username?.let { username = it }
            dto.email?.let { email = it }
            dto.password?.let {
                PasswordUtil.encode(it)?.let { encodePassword ->
                    password = encodePassword
                }
            }
        }

        saveAndFlushUserOrThrow(dbUser)

        //撤销refreshToken
        refreshRepository.revokeActiveTokenByUserId(dbUser.id, Instant.now())

        return dbUser
    }

    override fun deleteUserById(id: Long) {
        //根据id判断用户是否存在
        if (!userRepository.existsById(id)) throw NotFoundException(messages["message.user.user_not_found"])

        //删除用户
        userRepository.deleteById(id)
    }

    override fun getProfile(): User {
        //从上下文获取用户名
        val contextUsername = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw Exception("message.common.missing_user_context")

        //根据用户名获取用户
        return getUserByUsername(contextUsername)
    }

    @Transactional
    override fun updateProfile(dto: UpdateUserProfileDTO): User {
        //从上下文获取用户名
        val contextUsername = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw Exception("message.common.missing_user_context")

        //根据用户名获取用户
        val dbUser = getUserByUsername(contextUsername)

        dbUser.apply {
            dto.nickname?.let { nickname = it }
            dto.password?.let {
                PasswordUtil.encode(it)?.let { encodePassword ->
                    password = encodePassword
                }
            }
        }

        saveAndFlushUserOrThrow(dbUser)

        //撤销refreshToken
        refreshRepository.revokeActiveTokenByUserId(dbUser.id, Instant.now())

        return dbUser
    }

    private fun saveAndFlushUserOrThrow(dbUser: User): User {
        return try {
            userRepository.saveAndFlush(dbUser)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                UserConstraints.UNIQUE_USERNAME -> throw DbDuplicateException(dbUser.username)
                UserConstraints.UNIQUE_NICKNAME -> throw DbDuplicateException(dbUser.nickname)
                UserConstraints.UNIQUE_EMAIL -> throw DbDuplicateException(dbUser.email)
                else -> throw Exception(messages["message.common.unknown_error"])
            }
        }
    }
}