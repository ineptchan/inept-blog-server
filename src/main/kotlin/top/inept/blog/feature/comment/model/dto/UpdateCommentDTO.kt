package top.inept.blog.feature.comment.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.comment.model.validated.ValidatedCommentContent

data class UpdateCommentDTO(
    @field:Schema(description = "openapi.comment.content")
    @field:ValidatedCommentContent
    val content: String?,
)