package top.inept.blog.feature.article.model.convert

import top.inept.blog.feature.article.model.entity.Article
import top.inept.blog.feature.article.model.vo.ArticleSummaryVO
import top.inept.blog.feature.article.model.vo.ArticleTitleVO
import top.inept.blog.feature.article.model.vo.ArticleVO
import top.inept.blog.feature.article.model.vo.HomeArticleVO
import top.inept.blog.feature.article.repository.model.ArticleTitleDTO
import top.inept.blog.feature.categories.model.convert.toCategoriesVO
import top.inept.blog.feature.tag.model.convert.toTagVO
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

fun Article.toHomeArticleVO() = HomeArticleVO(
    id = this.id,
    title = this.title,
    slug = this.slug,
    homeContent = this.content.take(100),
    author = this.author.toUserPublicVO(),
    category = this.category.toCategoriesVO(),
    tags = this.tags.map { it.toTagVO() },
)