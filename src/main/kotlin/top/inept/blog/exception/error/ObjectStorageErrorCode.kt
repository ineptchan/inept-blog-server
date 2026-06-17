package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class ObjectStorageErrorCode(
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    ID_NOT_FOUND("message.object_storage.id_not_found", HttpStatus.NOT_FOUND),
    OBJECT_KEY_DB_DUPLICATE("message.object_storage.object_key_db_duplicate", HttpStatus.CONFLICT),
    SHA_256_DB_DUPLICATE("message.object_storage.sha_256_db_duplicate", HttpStatus.CONFLICT),
    NOT_IMAGE_FILE("message.object_storage.not_image_file", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    UNKNOWN_PURPOSE("message.object_storage.unknown_purpose", HttpStatus.BAD_REQUEST),
    CONTENT_SIZE_EXCEEDED("message.object_storage.content_size_exceeded", HttpStatus.PAYLOAD_TOO_LARGE),
    INVALID_UPLOAD_STATUS("message.object_storage.invalid_upload_status", HttpStatus.CONFLICT),
    UPLOAD_OWNER_MISMATCH("message.object_storage.upload_owner_mismatch", HttpStatus.FORBIDDEN),
    OBJECT_SIZE_INVALID("message.object_storage.object_size_invalid", HttpStatus.PAYLOAD_TOO_LARGE),
    OBJECT_SIZE_MISMATCH("message.object_storage.object_size_mismatch", HttpStatus.BAD_REQUEST),
    CONTENT_TYPE_MISMATCH("message.object_storage.content_type_mismatch", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    ARTICLE_ID_REQUIRED("message.object_storage.article_id_required", HttpStatus.BAD_REQUEST),

    PARSER_IMAGE_ERROR("message.object_storage.parser_image_error", HttpStatus.BAD_REQUEST),
    AVATAR_FILE_TOO_LARGE("message.object_storage.avatar_file_too_large", HttpStatus.PAYLOAD_TOO_LARGE),
    AVATAR_RESOLUTION_INVALID("message.object_storage.avatar_resolution_invalid", HttpStatus.UNPROCESSABLE_ENTITY),
    NOT_VIDEO_FILE("message.object_storage.not_video_file", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    REMOVE_ARTICLE_OBJECT_ERROR("message.object_storage.remove_article_object_error", HttpStatus.INTERNAL_SERVER_ERROR),
}