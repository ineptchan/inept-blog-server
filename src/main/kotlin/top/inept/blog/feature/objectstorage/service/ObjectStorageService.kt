package top.inept.blog.feature.objectstorage.service

import org.springframework.web.multipart.MultipartFile
import top.inept.blog.feature.article.model.dto.UploadArticleFeaturedImageDTO
import top.inept.blog.feature.article.model.dto.UploadArticleImageDTO
import top.inept.blog.feature.article.model.entity.Article

interface ObjectStorageService {
    fun saveAvatar(file: MultipartFile, ownerUserId: Long): String
    fun uploadArticleImage(ownerUserId: Long, ownerArticle: Article, dto: UploadArticleImageDTO): String
    fun uploadFeaturedImage(id: Long, article: Article, dto: UploadArticleFeaturedImageDTO): String
}