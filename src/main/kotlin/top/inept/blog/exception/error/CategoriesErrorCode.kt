package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class CategoriesErrorCode(
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    ID_NOT_FOUND("message.categories.id_not_found", HttpStatus.NOT_FOUND),
    NAME_DB_DUPLICATE("message.categories.name_db_duplicate", HttpStatus.CONFLICT),
    SLUG_DB_DUPLICATE("message.categories.slug_db_duplicate", HttpStatus.CONFLICT),
}