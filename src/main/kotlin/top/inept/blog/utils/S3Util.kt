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
        return "private/original/avatar/${now.year}/${now.monthValue}/${now.dayOfMonth}/$objectName.${ext}"
    }

    fun buildAvatarPrefix(objectName: String): String {
        val now = LocalDateTime.now()
        return "public/avatar/${now.year}/${now.monthValue}/${now.dayOfMonth}/$objectName.webp"
    }

    fun buildOriginalArticleImagePrefix(objectName: String, mime: String): String {
        val now = LocalDateTime.now()
        val ext = extFromMime(mime)
        return "private/original/article-image/${now.year}/${now.monthValue}/${now.dayOfMonth}/$objectName.${ext}"
    }

    fun buildArticleImagePrefix(objectName: String): String {
        val now = LocalDateTime.now()
        return "public/article-image/${now.year}/${now.monthValue}/${now.dayOfMonth}/$objectName.webp"
    }

    fun buildOriginalArticleFeaturedImagePrefix(objectName: String, mime: String): String {
        val now = LocalDateTime.now()
        val ext = extFromMime(mime)
        return "private/original/article-featured-image/${now.year}/${now.monthValue}/${now.dayOfMonth}/$objectName.${ext}"
    }

    fun buildArticleFeaturedImagePrefix(objectName: String): String {
        val now = LocalDateTime.now()
        return "public/article-featured-image/${now.year}/${now.monthValue}/${now.dayOfMonth}/$objectName.webp"
    }

    fun buildAvatarUrl(endpoint: String, bucket: String, objectKey: String) = "$endpoint$bucket/$objectKey"

    fun buildArticleImageUrl(endpoint: String, bucket: String, objectKey: String) = "$endpoint$bucket/$objectKey"

    fun buildArticleFeaturedImageUrl(endpoint: String, bucket: String, objectKey: String) =
        "$endpoint$bucket/$objectKey"
}