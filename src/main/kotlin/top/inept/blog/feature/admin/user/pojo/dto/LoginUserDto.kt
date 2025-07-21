package top.inept.blog.feature.admin.user.pojo.dto

import jakarta.validation.constraints.NotNull
import top.inept.blog.feature.admin.user.pojo.validated.ValidUsername
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedPassword

data class LoginUserDto(
    @field:ValidUsername
    @field:NotNull(message = "user.username_not_null")
    val username: String,

    @field:ValidatedPassword
    @field:NotNull(message = "user.password_not_null")
    val password: String,
)