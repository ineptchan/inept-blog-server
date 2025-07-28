package top.inept.blog.feature.admin.comment.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.PositiveOrZero
import top.inept.blog.feature.admin.comment.pojo.validated.ValidatedCommentContent

data class UpdateCommentDTO(
    @Schema(description = "openapi.comment.id")
    @field:PositiveOrZero(message = "valid.common.id")
    val id: Long,

    @Schema(description = "openapi.comment.content")
    @field:ValidatedCommentContent
    val content: String,
)