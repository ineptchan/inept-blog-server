package top.inept.blog.feature.admin.user.pojo.dto

import top.inept.blog.feature.admin.user.pojo.validated.ValidUsername
import top.inept.blog.feature.admin.user.pojo.validated.ValidatedPassword

data class LoginUserDto(
    @field:ValidUsername
    val username: String,

    @field:ValidatedPassword
    val password: String,
)