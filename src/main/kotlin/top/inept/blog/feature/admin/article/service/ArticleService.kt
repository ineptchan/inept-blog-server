package top.inept.blog.feature.admin.article.service

import top.inept.blog.feature.admin.article.pojo.dto.CreateArticleDTO
import top.inept.blog.feature.admin.article.pojo.dto.UpdateArticleDTO
import top.inept.blog.feature.admin.article.pojo.entity.Articles

interface ArticleService {
    fun getArticles(): List<Articles>
    fun getArticleById(id: Long): Articles
    fun createArticle(createArticleDTO: CreateArticleDTO): Articles
    fun updateArticle(updateArticleDTO: UpdateArticleDTO): Articles
    fun deleteArticle(id: Long)
}