package top.inept.blog.feature.user.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.user.model.validated.ValidateUserNickname
import top.inept.blog.feature.user.model.validated.ValidateUserUsername
import top.inept.blog.feature.user.model.validated.ValidatedUserEmail
import top.inept.blog.feature.user.model.validated.ValidatedUserPassword

data class UpdateUserDTO(
    @field:Schema(description = "openapi.user.nickname")
    @field:ValidateUserNickname
    val nickname: String?,

    @field:Schema(description = "openapi.user.username")
    @field:ValidateUserUsername
    val username: String?,

    @field:Schema(description = "openapi.user.password")
    @field:ValidatedUserPassword
    val password: String?,

    @field:Schema(description = "openapi.user.email")
    @field:ValidatedUserEmail
    val email: String?,

    @field:Schema(description = "openapi.user.role")
    val role: List<Long>?,
)