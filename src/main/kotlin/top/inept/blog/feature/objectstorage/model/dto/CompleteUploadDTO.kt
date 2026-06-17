package top.inept.blog.feature.objectstorage.model.dto

import io.swagger.v3.oas.annotations.media.Schema

data class CompleteUploadDTO(
    @field:Schema(description = "openapi.objectstorage.id")
    val id: Long,
)