package top.inept.blog.feature.admin.user.pojo.dto

import jakarta.validation.constraints.PositiveOrZero
import top.inept.blog.feature.admin.user.pojo.entity.enums.UserRole
import top.inept.blog.feature.admin.user.pojo.validated.ValidUsername
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedEmail
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedPassword

data class UserDto(
    @field:PositiveOrZero(message = "common.id")
    val id: Long = 0,

    @field:ValidUsername
    val username: String,

    @field:ValidatedPassword
    val password: String,

    @field:ValidatedEmail
    val email: String?,

    val role: UserRole
)