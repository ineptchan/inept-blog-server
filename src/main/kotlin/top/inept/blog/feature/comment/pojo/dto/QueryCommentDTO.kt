package top.inept.blog.feature.comment.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.base.BaseQueryDTO

data class QueryCommentDTO(
    @Schema(description = "openapi.comment.query_keyword")
    val keyword: String?,
) : BaseQueryDTO()