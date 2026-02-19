package top.inept.blog.feature.user.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class UserInfoVO(
    @field:Schema(description = "openapi.user.id")
    val id: Long,

    @field:Schema(description = "openapi.user.nickname")
    val nickname: String,

    @field:Schema(description = "openapi.user.avatar")
    val avatar: String,

    @field:Schema(description = "openapi.user.username")
    val username: String,

    @field:Schema(description = "openapi.user.email")
    val email: String?,

    @field:Schema(description = "openapi.permission.permission")
    val permissionCodes: List<String>
)