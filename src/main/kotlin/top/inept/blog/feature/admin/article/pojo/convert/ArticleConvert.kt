package top.inept.blog.feature.admin.article.pojo.convert

import top.inept.blog.feature.admin.article.pojo.entity.Articles
import top.inept.blog.feature.admin.article.pojo.vo.ArticleVO
import top.inept.blog.feature.admin.categories.pojo.convert.toCategoriesVO
import top.inept.blog.feature.admin.tag.pojo.convert.toTagVO
import top.inept.blog.feature.admin.user.pojo.convert.toUserVO

fun Articles.toArticleVO() = ArticleVO(
    id = this.id,
    title = this.title,
    slug = this.slug,
    content = this.content,
    author = this.author.toUserVO(),
    category = this.category.toCategoriesVO(),
    tags = this.tags.map { it.toTagVO() },
    articleStatus = this.articleStatus
)
