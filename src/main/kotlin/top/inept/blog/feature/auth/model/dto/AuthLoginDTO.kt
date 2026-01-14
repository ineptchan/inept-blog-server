package top.inept.blog.feature.auth.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.auth.model.validated.ValidateAuthUsername
import top.inept.blog.feature.auth.model.validated.ValidatedAuthPassword

data class AuthLoginDTO(
    @Schema(description = "openapi.user.username")
    @field:ValidateAuthUsername
    val username: String,

    @Schema(description = "openapi.user.password")
    @field:ValidatedAuthPassword
    val password: String,
)