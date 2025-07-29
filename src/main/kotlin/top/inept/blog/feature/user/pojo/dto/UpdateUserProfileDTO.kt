package top.inept.blog.feature.user.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.PositiveOrZero
import top.inept.blog.feature.user.pojo.validated.ValidateUserNickname
import top.inept.blog.feature.user.pojo.validated.ValidateUserUsername
import top.inept.blog.feature.user.pojo.validated.ValidatedUserPassword

data class UpdateUserProfileDTO(
    @Schema(description = "openapi.user.nickname")
    @field:ValidateUserNickname
    val nickname: String,

    @Schema(description = "openapi.user.password")
    @field:ValidatedUserPassword
    val password: String?,
)