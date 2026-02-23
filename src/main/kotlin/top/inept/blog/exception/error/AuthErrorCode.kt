package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class AuthErrorCode(
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    REFRESH_TOKEN_NOT_FOUND("message.auth.refresh_token_not_found", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_DB_NOT_FOUND("message.auth.refresh_token_db_not_found", HttpStatus.NOT_FOUND),
    USERNAME_OR_PASSWORD("message.auth.username_or_password", HttpStatus.BAD_REQUEST),
    TOKEN_HAS_BEEN_REVOKED("message.auth.token_has_been_revoked", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("message.auth.token_expired", HttpStatus.UNAUTHORIZED),
    TOKEN_VERIFICATION("message.auth.token_verification", HttpStatus.UNAUTHORIZED),
    TOKEN_SUBJECT_VERIFICATION("message.auth.token_subject_verification", HttpStatus.UNAUTHORIZED),
    TOKEN_USE_TYPE_VERIFICATION("message.auth.token_use_type_verification", HttpStatus.UNAUTHORIZED),
}