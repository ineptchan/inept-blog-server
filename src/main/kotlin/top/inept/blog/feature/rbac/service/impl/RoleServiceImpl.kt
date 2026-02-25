package top.inept.blog.feature.rbac.service.impl

import com.querydsl.core.BooleanBuilder
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.CommonErrorCode
import top.inept.blog.exception.error.RoleErrorCode
import top.inept.blog.extensions.toPageRequest
import top.inept.blog.feature.rbac.model.convert.toRole
import top.inept.blog.feature.rbac.model.dto.CreateRoleDTO
import top.inept.blog.feature.rbac.model.dto.QueryRoleDTO
import top.inept.blog.feature.rbac.model.dto.UpdateRoleDTO
import top.inept.blog.feature.rbac.model.entity.Permission
import top.inept.blog.feature.rbac.model.entity.QRole
import top.inept.blog.feature.rbac.model.entity.Role
import top.inept.blog.feature.rbac.model.entity.constraints.RoleConstraints
import top.inept.blog.feature.rbac.repository.RoleRepository
import top.inept.blog.feature.rbac.service.RoleService

@Service
class RoleServiceImpl(
    private val roleRepository: RoleRepository,
) : RoleService {
    override fun getRoles(dto: QueryRoleDTO): Page<Role> {
        val pageRequest = dto.toPageRequest()
        val r = QRole.role

        val builder = BooleanBuilder().apply {
            dto.keyword?.takeIf { it.isNotBlank() }?.let { kw ->
                and(r.name.contains(kw)).or(r.code.contains(kw)).or(r.description.contains(kw))
            }
        }

        return roleRepository.findAll(builder, pageRequest)
    }

    override fun createRole(dto: CreateRoleDTO): Role {
        return saveAndFlushTagOrThrow(dto.toRole())
    }

    override fun getRoleById(id: Long): Role {
        return roleRepository.findByIdOrNull(id)
            ?: throw BusinessException(RoleErrorCode.ID_NOT_FOUND, id)
    }

    override fun updateRole(
        id: Long,
        dto: UpdateRoleDTO
    ): Role {
        val dbRole = getRoleById(id)

        dbRole.apply {
            dto.code?.let { code = it }
            dto.name?.let { name = it }
            dto.description?.let { description = it }
        }

        saveAndFlushTagOrThrow(dbRole)

        return dbRole
    }

    override fun deleteRole(id: Long) {
        if (roleRepository.deleteRoleById(id) != 1L) {
            throw BusinessException(RoleErrorCode.ID_NOT_FOUND, id)
        }
    }

    override fun getRoleBindPermissions(id: Long): List<Permission> {
        val dbRole = roleRepository.findWithPermissionsById(id)
            ?: throw BusinessException(RoleErrorCode.ID_NOT_FOUND, id)

        return dbRole.permissionBindings.map { it.permission }
    }

    private fun saveAndFlushTagOrThrow(dbRole: Role): Role {
        return try {
            roleRepository.saveAndFlush(dbRole)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                RoleConstraints.UNIQUE_CODE -> throw BusinessException(RoleErrorCode.CODE_DB_DUPLICATE, dbRole.code)
                else -> throw BusinessException(CommonErrorCode.UNKNOWN)
            }
        }
    }
}