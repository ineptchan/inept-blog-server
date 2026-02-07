package top.inept.blog.feature.comment.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.PositiveOrZero
import top.inept.blog.feature.comment.model.validated.ValidatedCommentContent

data class CreateAnonymousCommentDTO(
    @field:Schema(description = "openapi.comment.content")
    @field:ValidatedCommentContent
    val content: String,

    @field:Schema(description = "openapi.comment.article_id")
    @field:PositiveOrZero(message = "valid.comment.article_cannot_be_empty")
    val articleId: Long?,

    @field:Schema(description = "openapi.comment.parent_comment")
    val parentCommentId: Long?,
)