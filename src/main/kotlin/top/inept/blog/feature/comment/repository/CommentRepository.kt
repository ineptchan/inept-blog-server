package top.inept.blog.feature.comment.repository

import org.springframework.data.jpa.repository.*
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import top.inept.blog.feature.comment.model.entity.Comment

@Repository
interface CommentRepository : JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment>,
    QuerydslPredicateExecutor<Comment> {

    @EntityGraph(attributePaths = ["user"])
    fun findCommentById(id: Long): Comment?

    @Modifying
    @Query("update Comment c set c.likeCount = c.likeCount + 1 where c.id = :id")
    fun increaseLikeCount(@Param("id") id: Long): Int

    @Modifying
    @Query("update Comment c set c.likeCount = c.likeCount - 1 where c.id = :id and c.likeCount > 0")
    fun decreaseLikeCount(@Param("id") id: Long): Int

    @Query("select c.likeCount from Comment c where c.id = :id")
    fun findCommentLikeCountById(@Param("id") id: Long): Long?
}