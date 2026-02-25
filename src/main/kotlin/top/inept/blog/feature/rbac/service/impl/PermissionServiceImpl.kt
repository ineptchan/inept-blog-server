package top.inept.blog.feature.rbac.service.impl

import com.querydsl.core.BooleanBuilder
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.CommonErrorCode
import top.inept.blog.exception.error.PermissionErrorCode
import top.inept.blog.extensions.toPageRequest
import top.inept.blog.feature.rbac.model.dto.QueryPermissionDTO
import top.inept.blog.feature.rbac.model.dto.UpdatePermissionDTO
import top.inept.blog.feature.rbac.model.entity.Permission
import top.inept.blog.feature.rbac.model.entity.QPermission
import top.inept.blog.feature.rbac.model.entity.constraints.PermissionConstraints
import top.inept.blog.feature.rbac.repository.PermissionRepository
import top.inept.blog.feature.rbac.service.PermissionService

@Service
class PermissionServiceImpl(
    private val permissionRepository: PermissionRepository
) : PermissionService {
    override fun getPermissions(dto: QueryPermissionDTO): Page<Permission> {
        val pageRequest = dto.toPageRequest()
        val p = QPermission.permission

        val builder = BooleanBuilder().apply {
            dto.keyword?.takeIf { it.isNotBlank() }?.let { kw ->
                and(p.name.contains(kw)).or(p.code.contains(kw)).or(p.description.contains(kw))
            }
        }

        return permissionRepository.findAll(builder, pageRequest)
    }

    override fun getPermissionById(id: Long): Permission {
        return permissionRepository.findByIdOrNull(id)
            ?: throw BusinessException(PermissionErrorCode.ID_NOT_FOUND, id)
    }

    override fun updatePermission(
        id: Long,
        dto: UpdatePermissionDTO
    ): Permission {
        val dbPermission = getPermissionById(id)

        dbPermission.apply {
            dto.name?.let { name = it }
            dto.description?.let { description = it }
        }

        saveAndFlushTagOrThrow(dbPermission)

        return dbPermission
    }

    private fun saveAndFlushTagOrThrow(dbPermission: Permission): Permission {
        return try {
            permissionRepository.saveAndFlush(dbPermission)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                PermissionConstraints.UNIQUE_CODE -> throw BusinessException(
                    PermissionErrorCode.CODE_DB_DUPLICATE,
                    dbPermission.code
                )

                PermissionConstraints.UNIQUE_NAME -> throw BusinessException(
                    PermissionErrorCode.NAME_DB_DUPLICATE, dbPermission.name
                )

                else -> throw BusinessException(CommonErrorCode.UNKNOWN)
            }
        }
    }
}
