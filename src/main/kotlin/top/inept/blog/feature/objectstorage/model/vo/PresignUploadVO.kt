package top.inept.blog.feature.objectstorage.model.vo

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class PresignUploadVO(
    @field:Schema(description = "openapi.objectstorage.id")
    val id: Long,

    @field:Schema(description = "openapi.objectstorage.bucket")
    val bucket: String,

    @field:Schema(description = "openapi.objectstorage.object_key")
    val objectKey: String,

    @field:Schema(description = "openapi.objectstorage.method")
    val method: String,

    @field:Schema(description = "openapi.objectstorage.url")
    val url: String,

    @field:Schema(description = "openapi.objectstorage.expires_in_seconds")
    val expiresInSeconds: Long,

    @field:Schema(description = "openapi.objectstorage.expires_at")
    val expiresAt: LocalDateTime,

    @field:Schema(description = "openapi.objectstorage.signed_headers")
    val signedHeaders: Map<String, String> = emptyMap()
)