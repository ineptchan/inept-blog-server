package top.inept.blog.feature.rbac.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class PermissionVO(
    @field:Schema(description = "openapi.permission.id")
    val id: Long,

    @field:Schema(description = "openapi.permission.code")
    val code: String,

    @field:Schema(description = "openapi.permission.name")
    val name: String,

    @field:Schema(description = "openapi.permission.description")
    val description: String?,
)