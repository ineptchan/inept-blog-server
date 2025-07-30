package top.inept.blog.feature.comment.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.PositiveOrZero
import top.inept.blog.feature.comment.pojo.validated.ValidatedCommentContent

data class CreateCommentDTO(
    @Schema(description = "openapi.comment.content")
    @field:ValidatedCommentContent
    val content: String,

    @Schema(description = "openapi.comment.article_id")
    @field:PositiveOrZero(message = "valid.comment.article_cannot_be_empty")
    val articleId: Long?,

    @Schema(description = "openapi.comment.parent_comment")
    val parentCommentId: Long?,
)