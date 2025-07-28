package top.inept.blog.feature.comment.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import top.inept.blog.feature.comment.pojo.entity.Comment

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {
}