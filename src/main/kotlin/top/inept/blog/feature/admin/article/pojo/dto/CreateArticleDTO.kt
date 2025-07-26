package top.inept.blog.feature.admin.article.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.PositiveOrZero
import top.inept.blog.feature.admin.article.pojo.validated.ValidatedArticleContent
import top.inept.blog.feature.admin.article.pojo.validated.ValidatedArticleSlug
import top.inept.blog.feature.admin.article.pojo.validated.ValidatedArticleTitle

data class CreateArticleDTO(
    @Schema(description = "openapi.article.title")
    @field:ValidatedArticleTitle
    val title: String,

    @Schema(description = "openapi.article.slug")
    @field:ValidatedArticleSlug
    val slug: String,

    @Schema(description = "openapi.article.content")
    @field:ValidatedArticleContent
    val content: String,

    @Schema(description = "openapi.article.author")
    @field:PositiveOrZero(message = "valid.article.author_cannot_be_empty")
    val authorId: Long,

    @Schema(description = "openapi.article.category")
    @field:PositiveOrZero(message = "valid.article.category_cannot_be_empty")
    val categoryId: Long,

    @Schema(description = "openapi.article.tags")
    val tagIds: List<Long>,
)