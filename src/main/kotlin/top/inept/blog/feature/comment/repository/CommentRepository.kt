package top.inept.blog.feature.comment.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import top.inept.blog.feature.comment.pojo.entity.Comment

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByArticle_IdAndParentCommentNull(id: Long): List<Comment>

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :parentId")
    fun countByParentCommentId(@Param("parentId") parentId: Long): Long
}