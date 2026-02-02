package top.inept.blog.feature.auth.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class RefreshVO(
    @field:Schema(description = "openai.auth.access_token")
    val accessToken: String
)