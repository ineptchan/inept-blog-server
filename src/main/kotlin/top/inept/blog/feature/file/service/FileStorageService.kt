package top.inept.blog.feature.file.service

import org.springframework.web.multipart.MultipartFile
import top.inept.blog.feature.file.model.vo.FileStorageVO

interface FileStorageService {
    fun upload(file: MultipartFile): FileStorageVO
    fun presignedGetUrl(id: Long): FileStorageVO
    fun presignedGetUrl(objectName: String): FileStorageVO
    fun delete(id: Long)
}