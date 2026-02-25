package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class RoleErrorCode(
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    ID_NOT_FOUND("message.role.id_not_found", HttpStatus.NOT_FOUND),
    CODE_DB_DUPLICATE("message.role.code_db_duplicate", HttpStatus.CONFLICT),
}