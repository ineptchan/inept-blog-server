package top.inept.blog.feature.rbac.service

import org.springframework.data.domain.Page
import top.inept.blog.feature.rbac.model.dto.CreateRoleDTO
import top.inept.blog.feature.rbac.model.dto.QueryRoleDTO
import top.inept.blog.feature.rbac.model.dto.UpdateRoleDTO
import top.inept.blog.feature.rbac.model.entity.Role

interface RoleService {
    fun getRoles(dto: QueryRoleDTO): Page<Role>
    fun createRole(dto: CreateRoleDTO): Role
    fun getRoleById(id: Long): Role
    fun updateRole(id: Long, dto: UpdateRoleDTO): Role
    fun deleteRole(id: Long)
}