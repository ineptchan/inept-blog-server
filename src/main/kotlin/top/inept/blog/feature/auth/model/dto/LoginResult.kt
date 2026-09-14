package top.inept.blog.feature.auth.model.dto

import top.inept.blog.feature.auth.model.vo.AuthLoginVO

data class LoginResult(
    val dto: AuthLoginVO,
    val refreshToken: String,
)