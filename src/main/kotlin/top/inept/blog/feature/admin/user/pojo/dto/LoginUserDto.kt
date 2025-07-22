package top.inept.blog.feature.admin.user.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.admin.user.pojo.validated.ValidUsername
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedPassword

data class LoginUserDto(
    @field:ValidUsername
    @Schema(description = "openapi.user.username")
    val username: String,

    @field:ValidatedPassword
    @Schema(description = "openapi.user.password")
    val password: String,
)