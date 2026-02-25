package top.inept.blog.feature.rbac.service

import org.springframework.data.domain.Page
import top.inept.blog.feature.rbac.model.dto.QueryPermissionDTO
import top.inept.blog.feature.rbac.model.dto.UpdatePermissionDTO
import top.inept.blog.feature.rbac.model.entity.Permission

interface PermissionService {
    fun getPermissions(dto: QueryPermissionDTO): Page<Permission>
    fun getPermissionById(id: Long): Permission
    fun updatePermission(id: Long, dto: UpdatePermissionDTO): Permission
}