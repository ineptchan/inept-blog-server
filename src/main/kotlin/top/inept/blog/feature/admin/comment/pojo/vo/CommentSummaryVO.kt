package top.inept.blog.feature.admin.comment.pojo.vo

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.admin.user.pojo.vo.UserPublicVO

data class CommentSummaryVO(
    @Schema(description = "openapi.comment.id")
    val id: Long,

    @Schema(description = "openapi.comment.content")
    val content: String,

    @Schema(description = "openapi.comment.user")
    val user: UserPublicVO,
)