package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class CommentErrorCode(
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    ID_NOT_FOUND("message.comment.id_not_found", HttpStatus.NOT_FOUND),
    PARENT_COMMENT_ID_NOT_FOUND("message.comment.parent_comment_id_not_found", HttpStatus.NOT_FOUND),
}