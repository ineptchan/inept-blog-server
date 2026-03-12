package top.inept.blog.feature.user.model.vo

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.rbac.model.vo.RoleVO

data class UserRolesVO(
    @field:Schema(description = "openapi.user.id")
    val id: Long,

    @field:Schema(description = "openapi.user.nickname")
    val nickname: String,

    @field:Schema(description = "openapi.user.username")
    val username: String,

    @field:Schema(description = "openapi.user.email")
    val email: String?,

    @field:Schema(description = "openapi.user.status")
    val status: Boolean,

    @field:Schema(description = "openapi.user.roles")
    val roles: List<RoleVO>
)