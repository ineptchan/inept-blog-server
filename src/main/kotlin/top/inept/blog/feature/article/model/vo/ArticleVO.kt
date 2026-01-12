package top.inept.blog.feature.article.model.vo

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.article.model.entity.enums.ArticleStatus
import top.inept.blog.feature.categories.model.vo.CategoriesVO
import top.inept.blog.feature.tag.pojo.vo.TagVO
import top.inept.blog.feature.user.pojo.vo.UserPublicVO

data class ArticleVO(
    @Schema(description = "openapi.article.id")
    val id: Long,

    @Schema(description = "openapi.article.title")
    val title: String,

    @Schema(description = "openapi.article.slug")
    val slug: String,

    @Schema(description = "openapi.article.content")
    val content: String,

    @Schema(description = "openapi.article.author")
    val author: UserPublicVO,

    @Schema(description = "openapi.article.category")
    val category: CategoriesVO,

    @Schema(description = "openapi.article.tags")
    val tags: List<TagVO>,

    @Schema(description = "openapi.article.article_status")
    val articleStatus: ArticleStatus,
)