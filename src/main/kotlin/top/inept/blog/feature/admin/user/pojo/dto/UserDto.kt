package top.inept.blog.feature.admin.user.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.PositiveOrZero
import top.inept.blog.feature.admin.user.pojo.entity.enums.UserRole
import top.inept.blog.feature.admin.user.pojo.validated.ValidUsername
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedEmail
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedPassword

data class UserDto(
    @field:PositiveOrZero(message = "common.id")
    @Schema(description = "openapi.user.id")
    val id: Long = 0,

    @field:ValidUsername
    @Schema(description = "openapi.user.username")
    val username: String,

    @field:ValidatedPassword
    @Schema(description = "openapi.user.password")
    val password: String,

    @field:ValidatedEmail
    @Schema(description = "openapi.user.email")
    val email: String?,

    @Schema(description = "openapi.user.role")
    val role: UserRole
)