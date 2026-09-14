package top.inept.blog.feature.auth.model.dto

data class RefreshResult(
    val accessToken: String,
    val refreshToken: String,
)