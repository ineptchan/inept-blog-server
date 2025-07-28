package top.inept.blog.feature.admin.user.pojo.vo

import io.swagger.v3.oas.annotations.media.Schema

data class UserVO(
    @Schema(description = "openapi.user.id")
    val id: Long,

    @Schema(description = "openapi.user.username")
    val username: String,

    @Schema(description = "openapi.user.email")
    val email: String?,
)