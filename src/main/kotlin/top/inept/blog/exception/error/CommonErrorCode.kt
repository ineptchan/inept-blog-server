package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class CommonErrorCode(
    override val code: String,
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    UNKNOWN("SYS_001", "message.common.unknown_error", HttpStatus.INTERNAL_SERVER_ERROR),
    ENTITY_NOT_FOUND("SYS_002", "message.common.entity_not_found", HttpStatus.NOT_FOUND),
    INVALID_DATA("SYS_003", "message.common.invalid_data", HttpStatus.BAD_REQUEST);
}