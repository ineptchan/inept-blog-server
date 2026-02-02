package top.inept.blog.feature.article.model.vo

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.article.model.entity.enums.ArticleStatus
import top.inept.blog.feature.categories.model.vo.CategoriesVO
import top.inept.blog.feature.tag.model.vo.TagVO
import top.inept.blog.feature.user.model.vo.UserPublicVO

data class HomeArticleVO(
    @field:Schema(description = "openapi.article.id")
    val id: Long,

    @field:Schema(description = "openapi.article.title")
    val title: String,

    @field:Schema(description = "openapi.article.slug")
    val slug: String,

    @field:Schema(description = "openapi.article.home_content")
    val homeContent: String,

    @field:Schema(description = "openapi.article.author")
    val author: UserPublicVO,

    @field:Schema(description = "openapi.article.category")
    val category: CategoriesVO,

    @field:Schema(description = "openapi.article.tags")
    val tags: List<TagVO>,

    @field:Schema(description = "openapi.article.article_status")
    val articleStatus: ArticleStatus
)