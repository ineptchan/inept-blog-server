package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class UserErrorCode(
    override val code: String,
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    NOT_FOUND("USER_001", "message.user.not_found", HttpStatus.NOT_FOUND),
    ID_NOT_FOUND("USER_002", "message.user.id_not_found", HttpStatus.NOT_FOUND),
    USERNAME_NOT_FOUND("USER_003", "message.user.username_not_found", HttpStatus.NOT_FOUND),
    USERNAME_MISSING_CONTEXT("USER_004", "message.user.username_missing_context", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_DB_DUPLICATE("USER_005", "message.user.username_db_duplicate", HttpStatus.CONFLICT),
    EMAIL_DB_DUPLICATE("USER_006", "message.user.email_db_duplicate", HttpStatus.CONFLICT),
}