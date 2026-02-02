package top.inept.blog.feature.file.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class FileStorageVO(
    @field:Schema(description = "openapi.file_storage.id")
    val id: Long,

    @field:Schema(description = "openapi.file_storage.url")
    val url: String,

    @field:Schema(description = "openapi.file_storage.object_name")
    val objectName: String,
)