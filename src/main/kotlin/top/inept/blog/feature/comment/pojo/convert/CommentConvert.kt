package top.inept.blog.feature.comment.pojo.convert

import top.inept.blog.feature.article.pojo.vo.ArticleTitleVO
import top.inept.blog.feature.comment.pojo.entity.Comment
import top.inept.blog.feature.comment.pojo.vo.CommentReplyVO
import top.inept.blog.feature.comment.pojo.vo.CommentSummaryVO
import top.inept.blog.feature.comment.pojo.vo.CommentVO
import top.inept.blog.feature.user.pojo.convert.toUserPublicVO

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