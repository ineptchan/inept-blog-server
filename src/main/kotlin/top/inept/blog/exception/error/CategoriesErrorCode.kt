package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class CategoriesErrorCode(
    override val code: String,
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    ID_NOT_FOUND("CATEGORIES_001", "message.categories.id_not_found", HttpStatus.NOT_FOUND),
    NAME_DB_DUPLICATE("CATEGORIES_002", "message.categories.name_db_duplicate", HttpStatus.CONFLICT),
    SLUG_DB_DUPLICATE("CATEGORIES_003", "message.categories.slug_db_duplicate", HttpStatus.CONFLICT),
}