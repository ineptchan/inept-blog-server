package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class CommonErrorCode(
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    UNKNOWN("message.common.unknown_error", HttpStatus.INTERNAL_SERVER_ERROR),
    ENTITY_NOT_FOUND("message.common.entity_not_found", HttpStatus.NOT_FOUND),
    INVALID_DATA("message.common.invalid_data", HttpStatus.BAD_REQUEST);
}