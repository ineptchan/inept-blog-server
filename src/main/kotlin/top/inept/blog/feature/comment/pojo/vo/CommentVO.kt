package top.inept.blog.feature.comment.pojo.vo

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.article.model.vo.ArticleTitleVO
import top.inept.blog.feature.user.pojo.vo.UserPublicVO
import java.time.LocalDateTime

data class CommentVO(
    @Schema(description = "openapi.comment.id")
    val id: Long,

    @Schema(description = "openapi.comment.content")
    val content: String,

    @Schema(description = "openapi.comment.article_id")
    val article: ArticleTitleVO,

    @Schema(description = "openapi.comment.user")
    val user: UserPublicVO,

    @Schema(description = "openapi.comment.parent_comment")
    val parentComment: CommentSummaryVO? = null,

    @Schema(description = "openapi.comment.createdAt")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,
)