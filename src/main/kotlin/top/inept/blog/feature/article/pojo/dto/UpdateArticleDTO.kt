package top.inept.blog.feature.article.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.PositiveOrZero
import top.inept.blog.feature.article.pojo.entity.enums.ArticleStatus
import top.inept.blog.feature.article.pojo.validated.ValidatedArticleContent
import top.inept.blog.feature.article.pojo.validated.ValidatedArticleSlug
import top.inept.blog.feature.article.pojo.validated.ValidatedArticleTitle

data class UpdateArticleDTO(
    @Schema(description = "openapi.article.id")
    @field:PositiveOrZero(message = "valid.common.id")
    val id: Long,

    @Schema(description = "openapi.article.title")
    @field:ValidatedArticleTitle
    val title: String,

    @Schema(description = "openapi.article.slug")
    @field:ValidatedArticleSlug
    val slug: String,

    @Schema(description = "openapi.article.content")
    @field:ValidatedArticleContent
    val content: String,

    @Schema(description = "openapi.article.category")
    @field:PositiveOrZero(message = "valid.article.category_cannot_be_empty")
    val categoryId: Long,

    @Schema(description = "openapi.article.tags")
    val tagIds: List<Long>,

    @Schema(description = "openapi.article.article_status")
    val articleStatus: ArticleStatus,
)