package top.inept.blog.feature.comment.model.vo

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.user.model.vo.UserPublicVO

data class CommentSummaryVO(
    @field:Schema(description = "openapi.comment.id")
    val id: Long,

    @field:Schema(description = "openapi.comment.content")
    val content: String,

    @field:Schema(description = "openapi.comment.user")
    val user: UserPublicVO,
)