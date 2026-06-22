package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class ArticleErrorCode(
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    ID_NOT_FOUND("message.articles.id_not_found", HttpStatus.NOT_FOUND),
    ID_NOT_FOUND_OR_NOT_PUBLIC("message.articles.id_not_found_or_not_public", HttpStatus.NOT_FOUND),
    SLUG_DB_DUPLICATE("message.articles.slug_db_duplicate", HttpStatus.CONFLICT),
    LIKE_ALREADY_EXISTS("message.articles.like_already_exists", HttpStatus.CONFLICT),
    LIKE_NOT_FOUND("message.articles.like_not_found", HttpStatus.CONFLICT),
}