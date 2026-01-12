package top.inept.blog.feature.article.service

import org.springframework.data.domain.Page
import top.inept.blog.feature.article.model.dto.QueryArticleDTO
import top.inept.blog.feature.article.repository.model.ArticleTitleDTO
import top.inept.blog.feature.article.model.dto.CreateArticleDTO
import top.inept.blog.feature.article.model.dto.UpdateArticleDTO
import top.inept.blog.feature.article.model.dto.UpdateArticleStatusDTO
import top.inept.blog.feature.article.model.entity.Article

interface ArticleService {
    fun getArticles(): List<Article>
    fun getArticleById(id: Long): Article
    fun createArticle(createArticleDTO: CreateArticleDTO): Article
    fun updateArticle(updateArticleDTO: UpdateArticleDTO): Article
    fun deleteArticle(id: Long)
    fun updateArticleStatus(updateArticleStatusDTO: UpdateArticleStatusDTO)
    fun getArticleTitleById(articleIds: List<Long>): List<ArticleTitleDTO>
    fun getArticleTitleById(articleId: Long): ArticleTitleDTO
    fun existsArticleById(id: Long): Boolean
    fun getHomeArticles(queryArticleDTO: QueryArticleDTO): Page<Article>
}