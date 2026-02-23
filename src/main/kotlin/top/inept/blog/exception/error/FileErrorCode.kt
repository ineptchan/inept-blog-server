package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class FileErrorCode(
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    ID_NOT_FOUND("message.file.id_not_found", HttpStatus.NOT_FOUND),
    OBJECT_NAME_NOT_FOUND("message.file.object_name_not_found", HttpStatus.NOT_FOUND),
    UPLOAD_FAILED("message.file.upload_failed", HttpStatus.INTERNAL_SERVER_ERROR),
    OBJECT_NAME_DB_DUPLICATE("message.file.object_name_db_duplicate", HttpStatus.CONFLICT),
    SHA256_DB_DUPLICATE("message.file.sha256_db_duplicate", HttpStatus.CONFLICT),
    FILE_ALREADY_DELETED("message.file.already_deleted", HttpStatus.GONE);
}
