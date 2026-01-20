package top.inept.blog.feature.file.service

import org.springframework.web.multipart.MultipartFile

interface FileStorageService {
    fun upload(file: MultipartFile): String
    fun presignedGetUrl(id: Long): String
    fun presignedGetUrl(objectName: String): String
    fun delete(id: Long)
}