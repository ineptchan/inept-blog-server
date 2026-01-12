package top.inept.blog.feature.user.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.user.model.validated.ValidateUserNickname
import top.inept.blog.feature.user.model.validated.ValidatedUserPassword

data class UpdateUserProfileDTO(
    @Schema(description = "openapi.user.nickname")
    @field:ValidateUserNickname
    val nickname: String,

    @Schema(description = "openapi.user.password")
    @field:ValidatedUserPassword
    val password: String?,
)