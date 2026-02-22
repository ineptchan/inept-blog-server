package top.inept.blog.exception.error

import org.springframework.http.HttpStatus
import top.inept.blog.exception.IErrorCode

enum class ObjectStorageErrorCode(
    override val code: String,
    override val messageKey: String,
    override val httpStatus: HttpStatus
) : IErrorCode {
    OBJECT_KEY_DB_DUPLICATE(
        "OBJECT_STORAGE_001",
        "message.object_storage.object_key_db_duplicate",
        HttpStatus.CONFLICT
    ),

    SHA_256_DB_DUPLICATE("OBJECT_STORAGE_002", "message.object_storage.sha_256_db_duplicate", HttpStatus.CONFLICT),

    NOT_IMAGE_FILE("OBJECT_STORAGE_003", "message.object_storage.not_image_file", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

    PARSER_IMAGE_ERROR("OBJECT_STORAGE_004", "message.object_storage.parser_image_error", HttpStatus.BAD_REQUEST),

    AVATAR_FILE_TOO_LARGE(
        "OBJECT_STORAGE_005",
        "message.object_storage.avatar_file_too_large",
        HttpStatus.PAYLOAD_TOO_LARGE
    ),

    AVATAR_RESOLUTION_INVALID(
        "OBJECT_STORAGE_006",
        "message.object_storage.avatar_resolution_invalid",
        HttpStatus.UNPROCESSABLE_ENTITY
    ),

    NOT_VIDEO_FILE("OBJECT_STORAGE_007", "message.object_storage.not_video_file", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

}