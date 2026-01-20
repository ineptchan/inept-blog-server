package top.inept.blog.feature.file.service

import org.springframework.web.multipart.MultipartFile
import top.inept.blog.feature.file.model.dto.MinioUploadDTO
import java.io.InputStream
import java.util.concurrent.TimeUnit

interface MinioService {
    fun ensureBucket()
    fun objectExists(objectName: String): Boolean
    fun upload(file: MultipartFile): MinioUploadDTO
    fun download(objectName: String): InputStream
    fun delete(objectName: String)
    fun presignedGetUrl(objectName: String, duration: Long, unit: TimeUnit): String
    fun presignedPutUrl(objectName: String, duration: Long, unit: TimeUnit): String
}