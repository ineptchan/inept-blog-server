package top.inept.blog.feature.article.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import top.inept.blog.feature.article.model.entity.ArticleLike

@Repository
interface ArticleLikeRepository : JpaRepository<ArticleLike, Long> {


    @Modifying
    @Query("delete from ArticleLike al where al.article.id =:articleId and al.user.id =:userId ")
    fun deleteByArticle_IdAndUser_Id(articleId: Long, userId: Long): Int


    fun countByArticle_Id(id: Long): Long
}