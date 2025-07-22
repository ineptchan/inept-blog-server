package top.inept.blog.feature.admin.user.pojo.vo

import io.swagger.v3.oas.annotations.media.Schema

data class LoginUserVo(
    @Schema(description = "openapi.user.id")
    val id: Long = 0,

    @Schema(description = "openapi.user.username")
    val username: String,

    @Schema(description = "openapi.user.token")
    val token: String,
)