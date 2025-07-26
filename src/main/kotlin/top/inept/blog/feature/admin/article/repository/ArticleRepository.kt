package top.inept.blog.feature.admin.article.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import top.inept.blog.feature.admin.article.pojo.entity.Article

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {
    fun existsBySlug(slug: String): Boolean
}