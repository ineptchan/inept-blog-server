package top.inept.blog.feature.rbac.model.convert

import top.inept.blog.feature.rbac.model.entity.Permission
import top.inept.blog.feature.rbac.model.vo.PermissionVO

fun Permission.toPermissionVO() = PermissionVO(
    id = this.id,
    code = this.code,
    name = this.name,
    description = this.description,
)