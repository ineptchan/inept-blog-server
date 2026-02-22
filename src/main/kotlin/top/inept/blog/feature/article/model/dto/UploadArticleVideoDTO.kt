package top.inept.blog.feature.article.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.multipart.MultipartFile

data class UploadArticleVideoDTO(
    @field:Schema(description = "openapi.article.video")
    val video: MultipartFile
)