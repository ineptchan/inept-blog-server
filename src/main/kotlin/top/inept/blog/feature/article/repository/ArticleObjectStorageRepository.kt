package top.inept.blog.feature.article.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import top.inept.blog.feature.article.model.entity.ArticleObjectStorage

@Repository
interface ArticleObjectStorageRepository : JpaRepository<ArticleObjectStorage, Long> {

    fun findByObjectStorage_Id(id: Long): ArticleObjectStorage?

    fun existsByObjectStorage_Id_AndArticle_Id(objectStorageId: Long, articleId: Long): Boolean
}