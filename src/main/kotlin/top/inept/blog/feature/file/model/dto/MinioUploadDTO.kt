package top.inept.blog.feature.file.model.dto

data class MinioUploadDTO(
    val originalFileName: String,
    val objectName: String,
    val sha256Hex: String,
    val mimeType: String,
    val bucket: String,
    val sizeBytes: Long
)