package top.inept.blog.feature.admin.user.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.admin.user.pojo.entity.enums.UserRole
import top.inept.blog.feature.admin.user.pojo.validated.ValidUsername
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedEmail
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedPassword

data class CreateUserDto(
    @Schema(description = "openapi.user.username")
    @field:ValidUsername
    val username: String,

    @Schema(description = "openapi.user.password")
    @field:ValidatedPassword
    val password: String,

    @Schema(description = "openapi.user.email")
    @field:ValidatedEmail
    val email: String?,

    @Schema(description = "openapi.user.role")
    val role: UserRole
)