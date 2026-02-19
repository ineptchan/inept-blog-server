package top.inept.blog.feature.user.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.multipart.MultipartFile
import top.inept.blog.feature.user.model.validated.ValidateUserNickname
import top.inept.blog.feature.user.model.validated.ValidatedUserPassword

data class UpdateUserProfileDTO(
    @field:Schema(description = "openapi.user.nickname")
    @field:ValidateUserNickname
    val nickname: String?,

    @field:Schema(description = "openapi.user.password")
    @field:ValidatedUserPassword
    val password: String?,

    @field:Schema(description = "openapi.user.avatar")
    val avatar: MultipartFile?,
)