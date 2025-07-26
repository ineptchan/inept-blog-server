package top.inept.blog.feature.admin.article.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import top.inept.blog.feature.admin.article.pojo.entity.Articles

@Repository
interface ArticleRepository : JpaRepository<Articles, Long> {
    fun existsBySlug(slug: String): Boolean
}