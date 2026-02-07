package top.inept.blog.feature.article.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.base.BaseQueryDTO
import top.inept.blog.feature.article.model.entity.enums.ArticleStatus

data class QueryArticleDTO(
    @field:Schema(description = "openapi.article.category")
    val category: Long?,

    @field:Schema(description = "openapi.article.query_keyword")
    val keyword: String?,

    @field:Schema(description = "openapi.article.tags")
    val tagIds: List<Long>?,

    @field:Schema(description = "openapi.article.article_status")
    val articleStatus: ArticleStatus?,
) : BaseQueryDTO()