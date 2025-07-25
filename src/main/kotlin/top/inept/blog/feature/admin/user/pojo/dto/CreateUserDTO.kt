package top.inept.blog.feature.admin.user.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.admin.user.pojo.entity.enums.UserRole
import top.inept.blog.feature.admin.user.pojo.validated.ValidateUserUsername
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedUserEmail
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedUserPassword

data class CreateUserDTO(
    @Schema(description = "openapi.user.username")
    @field:ValidateUserUsername
    val username: String,

    @Schema(description = "openapi.user.password")
    @field:ValidatedUserPassword
    val password: String,

    @Schema(description = "openapi.user.email")
    @field:ValidatedUserEmail
    val email: String?,

    @Schema(description = "openapi.user.role")
    val role: UserRole
)