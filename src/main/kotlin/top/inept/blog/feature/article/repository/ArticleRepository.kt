package top.inept.blog.feature.article.repository

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import top.inept.blog.feature.article.pojo.entity.Article
import top.inept.blog.feature.article.pojo.entity.enums.ArticleStatus
import top.inept.blog.feature.article.repository.model.ArticleTitleDTO

@Repository
interface ArticleRepository : JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    fun existsBySlug(slug: String): Boolean

    @Query("SELECT NEW top.inept.blog.feature.article.repository.model.ArticleTitleDTO(a.id, a.title) FROM Article a WHERE a.id IN :articleIds")
    fun findTitleAllById(articleIds: List<Long>): List<ArticleTitleDTO>

    @Query("SELECT NEW top.inept.blog.feature.article.repository.model.ArticleTitleDTO(a.id, a.title) FROM Article a WHERE a.id = :articleId")
    fun findTitleByIdOrNull(articleId: Long): ArticleTitleDTO?

    @Modifying
    @Transactional
    @Query("UPDATE Article a SET a.articleStatus = :status WHERE a.id IN :ids")
    fun updateStatusByIds(status: ArticleStatus, ids: List<Long>)
}