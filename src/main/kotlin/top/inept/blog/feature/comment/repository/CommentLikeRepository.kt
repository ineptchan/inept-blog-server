package top.inept.blog.feature.comment.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository
import top.inept.blog.feature.comment.model.entity.CommentLike

@Repository
interface CommentLikeRepository : JpaRepository<CommentLike, Long>, JpaSpecificationExecutor<CommentLike>,
    QuerydslPredicateExecutor<CommentLike> {

    fun existsByComment_IdAndUser_Id(commentId: Long, userId: Long): Boolean

    fun findByComment_IdAndUser_Id(commentId: Long, userId: Long): CommentLike?

    fun countById(id: Long): Long
}