package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class TagErrorCode(
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    ID_NOT_FOUND("message.tag.id_not_found", HttpStatus.NOT_FOUND),
    NAME_DB_DUPLICATE("message.tag.name_db_duplicate", HttpStatus.CONFLICT),
    SLUG_DB_DUPLICATE("message.tag.slug_db_duplicate", HttpStatus.CONFLICT),
}