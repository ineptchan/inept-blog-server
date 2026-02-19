package top.inept.blog.utils

import java.time.LocalDateTime

object S3Util {
    private fun extFromMime(mime: String): String =
        when (mime.lowercase()) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/gif" -> "gif"
            else -> "bin"
        }

    fun buildOriginalAvatarPrefix(objectName: String, mime: String): String {
        val now = LocalDateTime.now()
        val ext = extFromMime(mime)
        return "private/original_avatar/${now.year}/${now.monthValue}/${now.dayOfMonth}/$objectName.${ext}"
    }

    fun buildAvatarPrefix(objectName: String): String {
        val now = LocalDateTime.now()
        return "public/avatar/${now.year}/${now.monthValue}/${now.dayOfMonth}/$objectName.webp"
    }

    fun buildAvatarUrl(endpoint: String, bucket: String, objectKey: String) = "$endpoint$bucket/$objectKey"
}