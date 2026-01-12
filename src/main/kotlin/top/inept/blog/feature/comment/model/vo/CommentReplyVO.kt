package top.inept.blog.feature.comment.model.vo

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.user.model.vo.UserPublicVO
import java.time.LocalDateTime

data class CommentReplyVO(
    @Schema(description = "openapi.comment.id")
    val id: Long,

    @Schema(description = "openapi.comment.content")
    val content: String,

    @Schema(description = "openapi.comment.user")
    val user: UserPublicVO,

    @Schema(description = "openapi.comment.createdAt")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,
)