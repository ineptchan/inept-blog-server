package top.inept.blog.feature.admin.article.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import top.inept.blog.feature.admin.article.pojo.dto.ArticleTitleDTO
import top.inept.blog.feature.admin.article.pojo.entity.Article

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {
    fun existsBySlug(slug: String): Boolean

    @Query("SELECT NEW top.inept.blog.feature.admin.article.pojo.dto.ArticleTitleDTO(a.id, a.title) FROM Article a WHERE a.id IN :articleIds")
    fun findTitleAllById(articleIds: List<Long>):List<ArticleTitleDTO>

    @Query("SELECT NEW top.inept.blog.feature.admin.article.pojo.dto.ArticleTitleDTO(a.id, a.title) FROM Article a WHERE a.id = :articleId")
    fun findTitleByIdOrNull(articleId: Long):ArticleTitleDTO?
}