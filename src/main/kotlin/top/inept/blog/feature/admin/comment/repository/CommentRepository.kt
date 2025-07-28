package top.inept.blog.feature.admin.comment.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import top.inept.blog.feature.admin.comment.pojo.entity.Comment

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {
}