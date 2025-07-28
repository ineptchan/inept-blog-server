package top.inept.blog.feature.admin.article.pojo.vo

import io.swagger.v3.oas.annotations.media.Schema

data class ArticleTitleVO(
    @Schema(description = "openapi.article.id")
    val id: Long,

    @Schema(description = "openapi.article.title")
    val title: String,
)