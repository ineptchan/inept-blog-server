package top.inept.blog.feature.auth.model

import top.inept.blog.feature.auth.model.vo.AuthLoginVO

data class LoginBundle(
    val dto: AuthLoginVO,
    val refreshToken: String,
)