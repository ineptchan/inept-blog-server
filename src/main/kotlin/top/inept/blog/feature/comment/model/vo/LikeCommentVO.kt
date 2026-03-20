package top.inept.blog.feature.comment.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class LikeCommentVO(
    @field:Schema(description = "openapi.comment.liked")
    val liked: Boolean,

    @field:Schema(description = "openapi.comment.like_count")
    val likeCount: Long,
)