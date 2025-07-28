package top.inept.blog.feature.admin.comment.service

import top.inept.blog.feature.admin.comment.pojo.dto.CreateCommentDTO
import top.inept.blog.feature.admin.comment.pojo.dto.UpdateCommentDTO
import top.inept.blog.feature.admin.comment.pojo.vo.CommentReplyVO
import top.inept.blog.feature.admin.comment.pojo.vo.CommentSummaryVO
import top.inept.blog.feature.admin.comment.pojo.vo.CommentVO

interface CommentService {
    fun getComments(): List<CommentVO>
    fun getCommentById(id: Long): CommentVO
    fun createComment(createCommentDTO: CreateCommentDTO): CommentVO
    fun updateComment(updateCommentDTO: UpdateCommentDTO): CommentSummaryVO
    fun deleteComment(id: Long)
    fun getCommentReplies(id: Long): List<CommentReplyVO>
}