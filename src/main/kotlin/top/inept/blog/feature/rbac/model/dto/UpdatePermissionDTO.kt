package top.inept.blog.feature.rbac.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.rbac.model.validated.ValidatedRoleName

data class UpdatePermissionDTO(
    @field:Schema(description = "openapi.permission.name")
    @field:ValidatedRoleName
    val name: String?,

    @field:Schema(description = "openapi.permission.description")
    val description: String?,
)