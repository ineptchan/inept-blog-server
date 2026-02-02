package top.inept.blog.feature.comment.model.vo

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.article.model.vo.ArticleTitleVO
import top.inept.blog.feature.user.model.vo.UserLiteVO
import java.time.LocalDateTime

data class CommentVO(
    @field:Schema(description = "openapi.comment.id")
    val id: Long,

    @field:Schema(description = "openapi.comment.content")
    val content: String,

    @field:Schema(description = "openapi.comment.article_id")
    val article: ArticleTitleVO,

    @field:Schema(description = "openapi.comment.user")
    val user: UserLiteVO,

    @field:Schema(description = "openapi.comment.parent_comment")
    val parentComment: CommentSummaryVO? = null,

    @field:Schema(description = "openapi.comment.createdAt")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,
)