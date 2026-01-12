package top.inept.blog.feature.user.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.user.model.validated.ValidateUserUsername
import top.inept.blog.feature.user.model.validated.ValidatedUserPassword

data class LoginUserDTO(
    @Schema(description = "openapi.user.username")
    @field:ValidateUserUsername
    val username: String,

    @Schema(description = "openapi.user.password")
    @field:ValidatedUserPassword
    val password: String,
)