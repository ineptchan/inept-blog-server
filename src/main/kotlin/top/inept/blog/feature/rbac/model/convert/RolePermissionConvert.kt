package top.inept.blog.feature.rbac.model.convert

import top.inept.blog.feature.rbac.model.entity.Role
import top.inept.blog.feature.rbac.model.vo.RolePermissionVO

//TODO 小心sql N+1问题
fun Role.toRolePermissionVO() = RolePermissionVO(
    id = this.id,
    code = this.code,
    name = this.name,
    description = this.description,
    permissions = this.permissionBindings.map { it.permission.toPermissionVO() }
)