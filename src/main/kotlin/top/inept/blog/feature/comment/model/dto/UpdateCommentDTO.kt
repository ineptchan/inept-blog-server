package top.inept.blog.feature.comment.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.comment.model.entity.enums.CommentStatus

data class UpdateCommentDTO(
    @field:Schema(description = "openapi.comment.content")
    val content: String?,

    @field:Schema(description = "openapi.comment.status")
    val status: CommentStatus?,

    @field:Schema(description = "openapi.comment.like_count")
    val likeCount: Int?
)