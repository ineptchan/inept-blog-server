package top.inept.blog.feature.auth.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class AuthLoginVO(
    @field:Schema(description = "openapi.user.id")
    val id: Long,

    @field:Schema(description = "openapi.user.username")
    val username: String,

    @field:Schema(description = "openapi.user.nickname")
    val nickname: String,

    @field:Schema(description = "openapi.user.email")
    val email: String?,

    @field:Schema(description = "openai.auth.access_token")
    val accessToken: String,
)