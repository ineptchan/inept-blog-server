package top.inept.blog.feature.rbac.service

import org.springframework.data.domain.Page
import top.inept.blog.feature.rbac.model.dto.*
import top.inept.blog.feature.rbac.model.entity.Role
import top.inept.blog.feature.rbac.model.vo.RolePermissionVO

interface RoleService {
    fun getRoles(dto: QueryRoleDTO): Page<Role>
    fun createRole(dto: CreateRoleDTO): Role
    fun getRoleById(id: Long): Role
    fun updateRole(id: Long, dto: UpdateRoleDTO): Role
    fun deleteRole(id: Long)
    fun getRoleBindPermissions(id: Long): RolePermissionVO
    fun replaceRolePermissions(id: Long, dto: ReplaceRolePermissionsDTO): RolePermissionVO
    fun addRolePermissions(id: Long, dto: AddRolePermissionsDTO): RolePermissionVO
    fun removeRolePermission(roleId: Long, permId: Long): RolePermissionVO
}