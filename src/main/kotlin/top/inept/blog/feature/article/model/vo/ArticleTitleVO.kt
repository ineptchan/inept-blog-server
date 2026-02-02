package top.inept.blog.feature.article.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class ArticleTitleVO(
    @field:Schema(description = "openapi.article.id")
    val id: Long,

    @field:Schema(description = "openapi.article.title")
    val title: String,
)