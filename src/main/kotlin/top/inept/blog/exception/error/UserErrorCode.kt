package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class UserErrorCode(
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    NOT_FOUND("message.user.not_found", HttpStatus.NOT_FOUND),
    ID_NOT_FOUND("message.user.id_not_found", HttpStatus.NOT_FOUND),
    USERNAME_NOT_FOUND("message.user.username_not_found", HttpStatus.NOT_FOUND),
    USERNAME_MISSING_CONTEXT("message.user.username_missing_context", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_DB_DUPLICATE("message.user.username_db_duplicate", HttpStatus.CONFLICT),
    EMAIL_DB_DUPLICATE("message.user.email_db_duplicate", HttpStatus.CONFLICT),
    ROLE_NOT_FOUND("message.user.role_not_found", HttpStatus.NOT_FOUND),
}