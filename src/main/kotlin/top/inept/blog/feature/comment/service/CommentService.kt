package top.inept.blog.feature.comment.service

import top.inept.blog.base.BaseQueryDTO
import top.inept.blog.base.PageResponse
import top.inept.blog.feature.comment.model.dto.CreateAnonymousCommentDTO
import top.inept.blog.feature.comment.model.dto.CreateCommentDTO
import top.inept.blog.feature.comment.model.dto.QueryCommentDTO
import top.inept.blog.feature.comment.model.dto.UpdateCommentDTO
import top.inept.blog.feature.comment.model.entity.enums.CommentStatus
import top.inept.blog.feature.comment.model.vo.*

interface CommentService {
    fun getComments(dto: QueryCommentDTO): PageResponse<CommentVO>
    fun getCommentById(id: Long): CommentVO
    fun createComment(articleId: Long, dto: CreateCommentDTO): CommentVO
    fun updateComment(id: Long, dto: UpdateCommentDTO): CommentSummaryVO
    fun deleteComment(id: Long)
    fun getCommentReplies(commentId: Long, dto: BaseQueryDTO, status: CommentStatus?): PageResponse<CommentReplyVO>
    fun getTopComments(articleId: Long, dto: BaseQueryDTO): PageResponse<TopCommentVO>
    fun createAnonymousComment(dto: CreateAnonymousCommentDTO): CommentVO
    fun likeComment(commentId: Long): LikeCommentVO
    fun cancelLikeComment(commentId: Long): LikeCommentVO
}