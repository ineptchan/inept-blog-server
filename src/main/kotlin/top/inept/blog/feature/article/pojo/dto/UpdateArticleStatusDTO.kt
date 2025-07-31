package top.inept.blog.feature.article.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.PositiveOrZero
import top.inept.blog.feature.article.pojo.entity.enums.ArticleStatus

data class UpdateArticleStatusDTO(
    @Schema(description = "openapi.article.id")
    val articleIds: List<Long>,

    @Schema(description = "openapi.article.article_status")
    val articleStatus: ArticleStatus,
)