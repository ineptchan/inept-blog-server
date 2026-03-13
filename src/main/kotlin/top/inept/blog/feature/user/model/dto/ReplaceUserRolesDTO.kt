package top.inept.blog.feature.user.model.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ReplaceUserRolesDTO(
    @field:Schema(description = "openapi.user.roles")
    val roles: List<Long>
)