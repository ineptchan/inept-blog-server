package top.inept.blog.feature.rbac.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.rbac.model.validated.ValidatedRoleCode
import top.inept.blog.feature.rbac.model.validated.ValidatedRoleName

data class UpdateRoleDTO(
    @field:Schema(description = "openapi.role.code")
    @field:ValidatedRoleCode
    val code: String?,

    @field:Schema(description = "openapi.role.name")
    @field:ValidatedRoleName
    val name: String?,

    @field:Schema(description = "openapi.role.description")
    val description: String?,
)