package top.inept.blog.feature.article.service

import org.springframework.data.domain.Page
import top.inept.blog.feature.article.model.dto.CreateArticleDTO
import top.inept.blog.feature.article.model.dto.QueryArticleDTO
import top.inept.blog.feature.article.model.dto.UpdateArticleDTO
import top.inept.blog.feature.article.model.dto.UpdateArticleStatusDTO
import top.inept.blog.feature.article.model.entity.Article
import top.inept.blog.feature.article.repository.model.ArticleTitleDTO

interface ArticleService {
    fun getArticles(): List<Article>
    fun getArticleById(id: Long): Article
    fun createArticle(dto: CreateArticleDTO): Article
    fun updateArticle(id: Long, dto: UpdateArticleDTO): Article
    fun deleteArticle(id: Long)
    fun updateArticleStatus(dto: UpdateArticleStatusDTO)
    fun getArticleTitleById(articleIds: List<Long>): List<ArticleTitleDTO>
    fun getArticleTitleById(articleId: Long): ArticleTitleDTO
    fun existsArticleById(id: Long): Boolean
    fun getHomeArticles(dto: QueryArticleDTO): Page<Article>
}