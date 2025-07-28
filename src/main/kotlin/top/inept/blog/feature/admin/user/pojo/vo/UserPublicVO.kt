package top.inept.blog.feature.admin.user.pojo.vo

import io.swagger.v3.oas.annotations.media.Schema

data class UserPublicVO(
    @Schema(description = "openapi.user.id")
    val id: Long,

    @Schema(description = "openapi.user.nickname")
    val nickname: String,
)