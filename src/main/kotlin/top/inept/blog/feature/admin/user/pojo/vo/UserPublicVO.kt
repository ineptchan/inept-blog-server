package top.inept.blog.feature.admin.user.pojo.vo

import io.swagger.v3.oas.annotations.media.Schema

//TODO 添加name
data class UserPublicVO(
    @Schema(description = "openapi.user.id")
    val id: Long,

    @Schema(description = "openapi.user.username")
    val username: String,
)