package top.inept.blog.feature.user.service.impl

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import top.inept.blog.base.PageResponse
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.CommonErrorCode
import top.inept.blog.exception.error.RoleErrorCode
import top.inept.blog.exception.error.UserErrorCode
import top.inept.blog.extensions.toPageRequest
import top.inept.blog.feature.auth.repository.RefreshTokenRepository
import top.inept.blog.feature.objectstorage.service.ObjectStorageService
import top.inept.blog.feature.rbac.repository.RoleRepository
import top.inept.blog.feature.user.model.convert.toUserDetailVO
import top.inept.blog.feature.user.model.convert.toUserRolesVO
import top.inept.blog.feature.user.model.dto.*
import top.inept.blog.feature.user.model.entity.QUser
import top.inept.blog.feature.user.model.entity.User
import top.inept.blog.feature.user.model.entity.constraints.UserConstraints
import top.inept.blog.feature.user.model.vo.UserDetailVO
import top.inept.blog.feature.user.model.vo.UserRolesVO
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
    private val objectStorageService: ObjectStorageService,
    private val entityManager: EntityManager,
) : UserService {
    @Transactional(readOnly = true)
    override fun getUsers(dto: QueryUserDTO): PageResponse<UserRolesVO> {
        val sort = Sort.by(Sort.Direction.ASC, "id")
        val pageRequest = dto.toPageRequest(sort)

        val queryFactory = JPAQueryFactory(entityManager)
        val u = QUser.user

        //构建查询Predicate
        val builder = BooleanBuilder().apply {
            dto.keyword?.takeIf { it.isNotBlank() }?.let { kw ->
                and(
                    u.nickname.contains(kw)
                        .or(u.email.contains(kw))
                        .or(u.username.contains(kw))
                )
            }
        }

        //只查id列表
        val ids = queryFactory.select(u.id)
            .from(u)
            .where(builder)
            .orderBy(u.id.asc())
            .offset(pageRequest.offset)
            .limit(pageRequest.pageSize.toLong())
            .fetch()

        val total = queryFactory.select(u.count()).from(u).where(builder).fetchOne() ?: 0L

        //检查是不是空的，直接返回
        if (ids.isEmpty()) {
            return PageResponse.of(emptyList(), pageRequest, total)
        }

        //查完整的包含role
        val users = userRepository.findAllWithRolesByIdIn(ids, sort)

        return PageResponse.of(
            users.map { user -> user.toUserRolesVO() },
            pageRequest,
            total
        )
    }

    override fun getUserById(id: Long): User {
        return userRepository.findByIdOrNull(id)
            ?: throw BusinessException(UserErrorCode.ID_NOT_FOUND, id)
    }

    fun getUserWithRolesById(id: Long): User {
        return userRepository.findWithRolesById(id)
            ?: throw BusinessException(UserErrorCode.ID_NOT_FOUND, id)
    }

    @Transactional(readOnly = true)
    override fun getUserDetailById(id: Long): UserDetailVO {
        //根据id查找用户
        val dbUser = getUserWithRolesById(id)

        val permissionCodes = userRepository.findPermissionCodes(dbUser.id)

        return dbUser.toUserDetailVO(permissionCodes)
    }

    override fun getUserByUsername(username: String): User {
        //根据username查找用户
        return userRepository.findByUsername(username)
            ?: throw BusinessException(UserErrorCode.USERNAME_NOT_FOUND, username)
    }

    override fun getUserIdByUsername(username: String): Long? {
        return userRepository.findIdByUsername(username)
    }

    fun getUserWithRolesByUsername(username: String): User {
        //根据username查找用户
        return userRepository.findWithRolesByUsername(username)
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

                dbUser.addRoles(dbRoles)
            }
        }

        //TODO 推荐不要传入密码，系统随机生成发邮件通知

        //保存用户
        saveAndFlushOrThrow(dbUser)

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
            dto.status?.let { status = it }
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

                dbUser.replaceRoles(dbRoles)
            }
        }

        saveAndFlushOrThrow(dbUser)

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

    override fun getProfile(): UserDetailVO {
        //从上下文获取用户名
        val contextUsername = SecurityUtil.parseUsername(SecurityContextHolder.getContext())
            ?: throw BusinessException(UserErrorCode.USERNAME_MISSING_CONTEXT)

        //根据用户名获取用户
        val dbUser = getUserWithRolesByUsername(contextUsername)

        val permissionCodes = userRepository.findPermissionCodes(dbUser.id)

        return dbUser.toUserDetailVO(permissionCodes)
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
            //上传头像到s3中
            dto.avatar?.let { file ->
                //TODO 最好是异步上传
                avatar = objectStorageService.saveAvatar(file, dbUser.id)
            }
        }

        saveAndFlushOrThrow(dbUser)

        //撤销refreshToken
        if (dto.password != null) {
            refreshRepository.revokeActiveTokenByUserId(dbUser.id, Instant.now())
        }

        return dbUser
    }

    // === 用户角色 ===

    @Transactional
    override fun replaceUserRoles(
        id: Long,
        dto: ReplaceUserRolesDTO
    ): UserRolesVO {
        val dbUser = getUserWithRolesById(id)

        val targetIds = dto.roles.distinct()

        val roles = roleRepository.findAllById(targetIds)
        val rolesMap = roles.associateBy { it.id }

        //判断是否传入了数据库没有的id
        if (rolesMap.size != targetIds.size) {
            val missingIds = targetIds - rolesMap.keys
            throw BusinessException(RoleErrorCode.ID_NOT_FOUND, missingIds.joinToString())
        }

        dbUser.replaceRoles(roles)

        return saveAndFlushOrThrow(dbUser).toUserRolesVO()
    }

    override fun addUserRoles(
        id: Long,
        dto: AddUserRolesDTO
    ): UserRolesVO {
        val dbUser = getUserWithRolesById(id)

        val targetIds = dto.roles.distinct()

        val roles = roleRepository.findAllById(targetIds)
        val rolesMap = roles.associateBy { it.id }

        //判断是否传入了数据库没有的id
        if (rolesMap.size != targetIds.size) {
            val missingIds = targetIds - rolesMap.keys
            throw BusinessException(RoleErrorCode.ID_NOT_FOUND, missingIds.joinToString())
        }

        dbUser.addRoles(roles)

        return saveAndFlushOrThrow(dbUser).toUserRolesVO()
    }

    override fun removeUserRole(
        userId: Long,
        roleId: Long
    ): UserRolesVO {
        val dbUser = getUserWithRolesById(userId)

        val isRemove = dbUser.roleBindings.removeIf { it.role.id == roleId }
        if (!isRemove) throw BusinessException(UserErrorCode.USER_NOT_BINDING_ROLE, roleId)

        return saveAndFlushOrThrow(dbUser).toUserRolesVO()
    }

    private fun saveAndFlushOrThrow(dbUser: User): User {
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