package top.inept.blog.feature.admin.user.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.admin.user.pojo.validated.ValidateUserUsername
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedUserPassword

data class LoginUserDto(
    @Schema(description = "openapi.user.username")
    @field:ValidateUserUsername
    val username: String,

    @Schema(description = "openapi.user.password")
    @field:ValidatedUserPassword
    val password: String,
)