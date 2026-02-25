package top.inept.blog.feature.rbac.model.convert

import top.inept.blog.feature.rbac.model.dto.CreateRoleDTO
import top.inept.blog.feature.rbac.model.entity.Role
import top.inept.blog.feature.rbac.model.vo.RoleVO
import java.time.Instant

fun Role.toRoleVO() = RoleVO(
    id = this.id,
    name = this.name,
    code = this.code,
    description = this.description
)

fun CreateRoleDTO.toRole() = Role(
    code = this.code,
    name = this.name,
    description = this.description,
    createdAt = Instant.now()
)