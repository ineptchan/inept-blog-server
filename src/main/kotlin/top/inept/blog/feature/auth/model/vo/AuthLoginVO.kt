package top.inept.blog.feature.auth.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class AuthLoginVO(
    @Schema(description = "openapi.user.id")
    val id: Long,

    @Schema(description = "openapi.user.username")
    val username: String,

    @Schema(description = "openapi.user.nickname")
    val nickname: String,

    @Schema(description = "openapi.user.email")
    val email: String?,

    @Schema(description = "openapi.user.token")
    val accessToken: String,
)