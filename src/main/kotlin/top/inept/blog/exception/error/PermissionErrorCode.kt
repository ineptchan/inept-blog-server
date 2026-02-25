package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class PermissionErrorCode(
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    ID_NOT_FOUND("message.permission.id_not_found", HttpStatus.NOT_FOUND),
    CODE_DB_DUPLICATE("message.permission.code_db_duplicate", HttpStatus.CONFLICT),
    NAME_DB_DUPLICATE("message.permission.name_db_duplicate", HttpStatus.CONFLICT),
}