package top.inept.blog.feature.user.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class UserLiteVO(
    @field:Schema(description = "openapi.user.id")
    val id: Long,

    @field:Schema(description = "openapi.user.nickname")
    val nickname: String,
)