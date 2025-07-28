package top.inept.blog.feature.article.service

import top.inept.blog.feature.article.pojo.dto.ArticleTitleDTO
import top.inept.blog.feature.article.pojo.dto.CreateArticleDTO
import top.inept.blog.feature.article.pojo.dto.UpdateArticleDTO
import top.inept.blog.feature.article.pojo.dto.UpdateArticleStatusDTO
import top.inept.blog.feature.article.pojo.entity.Article

interface ArticleService {
    fun getArticles(): List<Article>
    fun getArticleById(id: Long): Article
    fun createArticle(createArticleDTO: CreateArticleDTO): Article
    fun updateArticle(updateArticleDTO: UpdateArticleDTO): Article
    fun deleteArticle(id: Long)
    fun updateArticleStatus(updateArticleStatusDTO: UpdateArticleStatusDTO): Article
    fun getArticleTitleById(articleIds: List<Long>): List<ArticleTitleDTO>
    fun getArticleTitleById(articleId: Long): ArticleTitleDTO
    fun existsArticleById(id: Long): Boolean
}