package top.inept.blog.feature.admin.article.pojo.convert

import top.inept.blog.feature.admin.article.pojo.dto.ArticleTitleDTO
import top.inept.blog.feature.admin.article.pojo.entity.Article
import top.inept.blog.feature.admin.article.pojo.vo.ArticleTitleVO
import top.inept.blog.feature.admin.article.pojo.vo.ArticleVO
import top.inept.blog.feature.admin.categories.pojo.convert.toCategoriesVO
import top.inept.blog.feature.admin.tag.pojo.convert.toTagVO
import top.inept.blog.feature.admin.user.pojo.convert.toUserPublicVO

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

fun ArticleTitleDTO.toArticleTitleVO() = ArticleTitleVO(
    id = this.id,
    title = this.title,
)