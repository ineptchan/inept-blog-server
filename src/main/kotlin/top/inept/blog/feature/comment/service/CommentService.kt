package top.inept.blog.feature.comment.service

import top.inept.blog.base.BaseQueryDTO
import top.inept.blog.base.PageResponse
import top.inept.blog.feature.comment.model.dto.CreateAnonymousCommentDTO
import top.inept.blog.feature.comment.model.dto.CreateCommentDTO
import top.inept.blog.feature.comment.model.dto.QueryCommentDTO
import top.inept.blog.feature.comment.model.dto.UpdateCommentDTO
import top.inept.blog.feature.comment.model.vo.CommentReplyVO
import top.inept.blog.feature.comment.model.vo.CommentSummaryVO
import top.inept.blog.feature.comment.model.vo.CommentVO
import top.inept.blog.feature.comment.model.vo.TopCommentVO

interface CommentService {
    fun getComments(queryCommentDTO: QueryCommentDTO): PageResponse<CommentVO>
    fun getCommentById(id: Long): CommentVO
    fun createComment(createCommentDTO: CreateCommentDTO): CommentVO
    fun updateComment(updateCommentDTO: UpdateCommentDTO): CommentSummaryVO
    fun deleteComment(id: Long)
    fun getCommentReplies(commentId: Long, baseQueryDTO: BaseQueryDTO): PageResponse<CommentReplyVO>
    fun getTopComments(articleId: Long, baseQueryDTO: BaseQueryDTO): PageResponse<TopCommentVO>
    fun createAnonymousComment(createAnonymousCommentDTO: CreateAnonymousCommentDTO): CommentVO
}