package top.inept.blog.feature.objectstorage.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive
import top.inept.blog.feature.objectstorage.model.entity.enums.Purpose

data class PresignUploadDTO(
    @field:Schema(description = "openapi.objectstorage.purpose")
    val purpose: Purpose,

    @field:Schema(description = "openapi.objectstorage.content_type")
    val contentType: String,

    @field:Schema(description = "openapi.objectstorage.file_size")
    @field:Positive(message = "valid.objectstorage.file_size")
    val fileSize: Long,

    @field:Schema(description = "openapi.objectstorage.file_name")
    val fileName: String,

    @field:Schema(description = "openapi.article.id")
    val articleId: Long? = null,
)