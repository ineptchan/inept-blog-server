package top.inept.blog.feature.admin.user.pojo.dto

import top.inept.blog.feature.admin.user.pojo.validated.ValidUsername
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedPassword
import jakarta.validation.constraints.PositiveOrZero

data class UserDto(
    @field:PositiveOrZero(message = "错误的id")
    val id: Long = 0,

    @field:ValidUsername
    val username: String,

    @field:ValidatedPassword
    val password: String,
)