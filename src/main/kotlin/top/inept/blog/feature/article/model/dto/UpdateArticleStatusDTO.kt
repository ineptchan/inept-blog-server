package top.inept.blog.feature.article.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.article.model.entity.enums.ArticleStatus

data class UpdateArticleStatusDTO(
    @Schema(description = "openapi.article.id")
    val articleIds: List<Long>,

    @Schema(description = "openapi.article.article_status")
    val articleStatus: ArticleStatus,
)