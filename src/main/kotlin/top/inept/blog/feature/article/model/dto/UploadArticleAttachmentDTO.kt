package top.inept.blog.feature.article.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.multipart.MultipartFile

data class UploadArticleAttachmentDTO(
    @field:Schema(description = "openapi.article.attachment")
    val attachment: MultipartFile
)