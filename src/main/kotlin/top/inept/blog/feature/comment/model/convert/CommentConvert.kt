package top.inept.blog.feature.comment.model.convert

import top.inept.blog.feature.article.model.vo.ArticleTitleVO
import top.inept.blog.feature.comment.model.entity.Comment
import top.inept.blog.feature.comment.model.vo.CommentReplyVO
import top.inept.blog.feature.comment.model.vo.CommentSummaryVO
import top.inept.blog.feature.comment.model.vo.CommentVO
import top.inept.blog.feature.comment.model.vo.TopCommentVO
import top.inept.blog.feature.user.model.convert.toUserPublicVO

fun Comment.toCommentVO(articleTitleVO: ArticleTitleVO) = CommentVO(
    id = this.id,
    content = this.content,
    article = articleTitleVO,
    user = this.user.toUserPublicVO(),
    parentComment = this.parentComment?.toCommentSummaryVO(),
    createdAt = this.createdAt,
)

fun Comment.toCommentSummaryVO() = CommentSummaryVO(
    id = this.id,
    content = this.content,
    user = this.user.toUserPublicVO(),
)

fun Comment.toCommentReplyVO() = CommentReplyVO(
    id = this.id,
    content = this.content,
    user = this.user.toUserPublicVO(),
    createdAt = this.createdAt,
)

fun Comment.toTopCommentVO(replyTotal: Long) = TopCommentVO(
    id = this.id,
    content = this.content,
    user = this.user.toUserPublicVO(),
    replyTotal = replyTotal,
    createdAt = this.createdAt,
)