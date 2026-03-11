package top.inept.blog.feature.rbac.model.dto

import io.swagger.v3.oas.annotations.media.Schema

data class AddRolePermissionsDTO(
    @field:Schema(description = "openapi.role.permissions")
    val permissions: List<Long>
)