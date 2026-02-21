package top.inept.blog.feature.article.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.multipart.MultipartFile

data class UploadArticleImageDTO(
    @field:Schema(description = "openapi.article.image")
    val image: MultipartFile
)