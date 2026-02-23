package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class ArticleErrorCode(
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    ID_NOT_FOUND("message.articles.id_not_found", HttpStatus.NOT_FOUND),
    SLUG_DB_DUPLICATE("message.articles.slug_db_duplicate", HttpStatus.CONFLICT),
}