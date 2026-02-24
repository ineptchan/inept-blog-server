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
    fun getComments(dto: QueryCommentDTO): PageResponse<CommentVO>
    fun getCommentById(id: Long): CommentVO
    fun createComment(dto: CreateCommentDTO): CommentVO
    fun updateComment(id: Long, dto: UpdateCommentDTO): CommentSummaryVO
    fun deleteComment(id: Long)
    fun getCommentReplies(commentId: Long, dto: BaseQueryDTO): PageResponse<CommentReplyVO>
    fun getTopComments(articleId: Long, dto: BaseQueryDTO): PageResponse<TopCommentVO>
    fun createAnonymousComment(dto: CreateAnonymousCommentDTO): CommentVO
}