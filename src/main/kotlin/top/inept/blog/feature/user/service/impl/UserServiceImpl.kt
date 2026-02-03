package top.inept.blog.feature.user.service.impl

import com.querydsl.core.BooleanBuilder
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.CommonErrorCode
import top.inept.blog.exception.error.UserErrorCode
import top.inept.blog.extensions.toPageRequest
import top.inept.blog.feature.auth.repository.RefreshTokenRepository
import top.inept.blog.feature.user.model.convert.toUserInfoVO
import top.inept.blog.feature.user.model.dto.CreateUserDTO
import top.inept.blog.feature.user.model.dto.QueryUserDTO
import top.inept.blog.feature.user.model.dto.UpdateUserDTO
import top.inept.blog.feature.user.model.dto.UpdateUserProfileDTO
import top.inept.blog.feature.user.model.entity.QUser
import top.inept.blog.feature.user.model.entity.User
import top.inept.blog.feature.user.model.entity.constraints.UserConstraints
import top.inept.blog.feature.user.model.vo.UserInfoVO
import top.inept.blog.feature.user.repository.RoleRepository
import top.inept.blog.feature.user.repository.UserRepository
import top.inept.blog.feature.user.service.UserService
import top.inept.blog.utils.PasswordUtil
import top.inept.blog.utils.SecurityUtil
import java.time.Instant

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val refreshRepository: RefreshTokenRepository,
    private val roleRepository: RoleRepository,
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
        return userRepository.findByIdOrNull(id)
            ?: throw BusinessException(UserErrorCode.ID_NOT_FOUND, id)
    }

    override fun getUserInfoById(id: Long): UserInfoVO {
        //根据id查找用户
        val dbUser = userRepository.findByIdOrNull(id)
            ?: throw BusinessException(UserErrorCode.ID_NOT_FOUND, id)

        val permissionCodes = userRepository.findPermissionCodes(dbUser.id)

        return dbUser.toUserInfoVO(permissionCodes)
    }

    override fun getUserByUsername(username: String): User {
        //根据username查找用户
        return userRepository.findByUsername(username)
            ?: throw BusinessException(UserErrorCode.USERNAME_NOT_FOUND, username)
    }

    @Transactional
    override fun createUser(dto: CreateUserDTO): User {
        val encodePassword = PasswordUtil.encode(dto.password)
            ?: throw BusinessException(CommonErrorCode.UNKNOWN)

        val dbUser = User(
            username = dto.username,
            nickname = dto.nickname,
            email = dto.email,
            password = encodePassword
        )

        dto.role?.let { roles ->
            if (roles.isNotEmpty()) {
                val dbRoles = roleRepository.findAllById(dto.role)

                //判断是否都可用
                if (dto.role.size != dbRoles.size) {
                    //role转成long
                    val ids = dbRoles.map { it.id }
                    val notFind = dto.role
                        .filter { it !in ids }
                        .joinToString(prefix = "(", postfix = ")")

                    throw BusinessException(UserErrorCode.ROLE_NOT_FOUND, notFind)
                }

                dbUser.bindRoles(dbRoles)
            }
        }

        //TODO 推荐不要传入密码，系统随机生成发邮件通知

        //保存用户
        saveAndFlushUserOrThrow(dbUser)

        return dbUser
    }

    @Transactional
    override fun updateUser(id: Long, dto: UpdateUserDTO): User {
        //根据id查找用户
        val dbUser = getUserById(id)

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

        //角色相关
        dto.role?.let { roles ->
            if (roles.isNotEmpty()) {
                val dbRoles = roleRepository.findAllById(roles)

                //判断是否都可用
                if (dto.role.size != dbRoles.size) {
                    //role转成long
                    val ids = dbRoles.map { it.id }
                    val notFind = dto.role
                        .filter { it !in ids }
                        .joinToString(prefix = "(", postfix = ")")

                    throw BusinessException(UserErrorCode.ROLE_NOT_FOUND, notFind)
                }

                dbUser.updateRoles(dbRoles)
            }
        }

        saveAndFlushUserOrThrow(dbUser)

        //撤销refreshToken
        refreshRepository.revokeActiveTokenByUserId(dbUser.id, Instant.now())

        return dbUser
    }

    override fun deleteUserById(id: Long) {
        //根据id判断用户是否存在
        if (!userRepository.existsById(id)) throw BusinessException(UserErrorCode.ID_NOT_FOUND, id)

        //删除用户
        userRepository.deleteById(id)
    }

    override fun getProfile(): UserInfoVO {
        //从上下文获取用户名
        val contextUsername = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw BusinessException(UserErrorCode.USERNAME_MISSING_CONTEXT)

        //根据用户名获取用户
        val dbUser = getUserByUsername(contextUsername)

        val permissionCodes = userRepository.findPermissionCodes(dbUser.id)

        return dbUser.toUserInfoVO(permissionCodes)
    }

    @Transactional
    override fun updateProfile(dto: UpdateUserProfileDTO): User {
        //从上下文获取用户名
        val contextUsername = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw BusinessException(UserErrorCode.USERNAME_MISSING_CONTEXT)

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
                UserConstraints.UNIQUE_USERNAME ->
                    throw BusinessException(UserErrorCode.USERNAME_DB_DUPLICATE, dbUser.username)

                UserConstraints.UNIQUE_EMAIL ->
                    throw BusinessException(UserErrorCode.EMAIL_DB_DUPLICATE, dbUser.email ?: "null")

                else -> throw BusinessException(CommonErrorCode.UNKNOWN)
            }
        }
    }
}