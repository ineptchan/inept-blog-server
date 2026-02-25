package top.inept.blog.feature.rbac.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class RoleVO(
    @field:Schema(description = "openapi.role.id")
    val id: Long,

    @field:Schema(description = "openapi.role.code")
    val code: String,

    @field:Schema(description = "openapi.role.name")
    val name: String,

    @field:Schema(description = "openapi.role.description")
    val description: String?,
)