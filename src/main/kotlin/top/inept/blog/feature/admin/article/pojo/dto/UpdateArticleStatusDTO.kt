package top.inept.blog.feature.admin.article.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.admin.article.pojo.entity.enums.ArticleStatus

data class UpdateArticleStatusDTO(
    @Schema(description = "openapi.article.id")
    val id: Long,

    @Schema(description = "openapi.article.article_status")
    val articleStatus: ArticleStatus,
)