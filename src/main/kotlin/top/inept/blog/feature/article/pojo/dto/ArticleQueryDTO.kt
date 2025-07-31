package top.inept.blog.feature.article.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive
import org.hibernate.validator.constraints.Range
import top.inept.blog.feature.article.pojo.entity.enums.ArticleStatus

data class ArticleQueryDTO(
    @Schema(description = "openapi.query.page")
    @field:Positive(message = "valid.common.query.page")
    val page: Int = 1,

    @Schema(description = "openapi.query.size")
    @field:Range(min = 1, max = 100, message = "valid.common.query.size")
    val size: Int = 30,

    @Schema(description = "openapi.article.category")
    val category: Long?,

    @Schema(description = "openapi.article.title")
    val title: String?,

    @Schema(description = "openapi.article.content")
    val content: String?,

    @Schema(description = "openapi.article.tags")
    val tagIds: List<Long>?,

    @Schema(description = "openapi.article.article_status")
    val articleStatus: ArticleStatus?,
)