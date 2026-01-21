package top.inept.blog.feature.file.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class FileStorageVO(
    @Schema(description = "openapi.file_storage.id")
    val id: Long,

    @Schema(description = "openapi.file_storage.url")
    val url: String,

    @Schema(description = "openapi.file_storage.object_name")
    val objectName: String,
)