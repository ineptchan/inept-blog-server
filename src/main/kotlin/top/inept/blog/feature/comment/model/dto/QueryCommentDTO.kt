package top.inept.blog.feature.comment.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.base.BaseQueryDTO
import top.inept.blog.feature.comment.model.entity.enums.CommentStatus

data class QueryCommentDTO(
    @field:Schema(description = "openapi.comment.query_keyword")
    val keyword: String?,

    @field:Schema(description = "openapi.comment.status")
    val status: CommentStatus?,
) : BaseQueryDTO()