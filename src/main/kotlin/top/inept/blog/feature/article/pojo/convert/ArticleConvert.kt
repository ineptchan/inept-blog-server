package top.inept.blog.feature.article.pojo.convert

import top.inept.blog.feature.article.pojo.entity.Article
import top.inept.blog.feature.article.pojo.vo.ArticleSummaryVO
import top.inept.blog.feature.article.pojo.vo.ArticleTitleVO
import top.inept.blog.feature.article.pojo.vo.ArticleVO
import top.inept.blog.feature.article.repository.model.ArticleTitleDTO
import top.inept.blog.feature.categories.pojo.convert.toCategoriesVO
import top.inept.blog.feature.tag.pojo.convert.toTagVO
import top.inept.blog.feature.user.pojo.convert.toUserPublicVO

fun Article.toArticleVO() = ArticleVO(
    id = this.id,
    title = this.title,
    slug = this.slug,
    content = this.content,
    author = this.author.toUserPublicVO(),
    category = this.category.toCategoriesVO(),
    tags = this.tags.map { it.toTagVO() },
    articleStatus = this.articleStatus
)

fun Article.toArticleSummaryVO() = ArticleSummaryVO(
    id = this.id,
    title = this.title,
    slug = this.slug,
    author = this.author.toUserPublicVO(),
    category = this.category.toCategoriesVO(),
    tags = this.tags.map { it.toTagVO() },
    articleStatus = this.articleStatus
)

fun ArticleTitleDTO.toArticleTitleVO() = ArticleTitleVO(
    id = this.id,
    title = this.title,
)

fun Article.toArticleTitleVO() = ArticleTitleVO(
    id = this.id,
    title = this.title,
)