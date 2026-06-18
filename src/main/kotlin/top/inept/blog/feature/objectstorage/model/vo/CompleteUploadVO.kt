package top.inept.blog.feature.objectstorage.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class CompleteUploadVO(
    @field:Schema(description = "openapi.objectstorage.id")
    val id: Long,

    @field:Schema(description = "openapi.objectstorage.bucket")
    val bucket: String,

    @field:Schema(description = "openapi.objectstorage.object_key")
    val objectKey: String,

    @field:Schema(description = "openapi.objectstorage.url")
    val url: String,
)