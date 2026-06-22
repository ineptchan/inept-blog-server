package top.inept.blog.feature.article.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository
import top.inept.blog.feature.article.model.entity.Article
import top.inept.blog.feature.article.model.entity.enums.ArticleStatus
import top.inept.blog.feature.article.model.entity.model.ArticleTitleDTO
import top.inept.blog.feature.article.model.entity.model.LikeCountDTO

@Repository
interface ArticleRepository : JpaRepository<Article, Long>, JpaSpecificationExecutor<Article>,
    QuerydslPredicateExecutor<Article> {
    fun existsBySlug(slug: String): Boolean

    @Query("SELECT NEW top.inept.blog.feature.article.model.entity.model.ArticleTitleDTO(a.id, a.title) FROM Article a WHERE a.id IN :articleIds")
    fun findTitleAllById(articleIds: List<Long>): List<ArticleTitleDTO>

    @Query("SELECT NEW top.inept.blog.feature.article.model.entity.model.ArticleTitleDTO(a.id, a.title) FROM Article a WHERE a.id = :articleId")
    fun findTitleByIdOrNull(articleId: Long): ArticleTitleDTO?

    @Modifying
    @Query("UPDATE Article a SET a.articleStatus = :status WHERE a.id IN :ids")
    fun updateStatusByIds(status: ArticleStatus, ids: List<Long>)

    @Modifying
    @Query("update Article a set a.featuredImage = :url where a.id = :id")
    fun updateFeaturedImageById(id: Long, url: String)

    fun findByIdAndArticleStatus(id: Long, articleStatus: ArticleStatus): Article?

    @Query("select new top.inept.blog.feature.article.model.entity.model.LikeCountDTO(a.id,a.likeCount) from Article a where a.id = :id")
    fun findLikeCountById(id: Long): LikeCountDTO?

    @Modifying
    @Query("update Article a set a.likeCount = a.likeCount + 1 where a.id = :id")
    fun increaseLikeCount(id: Long): Int

    @Modifying
    @Query("update Article a set a.likeCount = a.likeCount - 1 where a.id = :id and a.likeCount > 0")
    fun decreaseLikeCount(id: Long): Int

    @Modifying
    @Query("update Article a set a.likeCount = ?2 where a.id = ?1")
    fun updateLikeCountById(articleId: Long, likeCount: Long): Int
}