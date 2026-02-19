package top.inept.blog.feature.objectstorage.service

import org.springframework.web.multipart.MultipartFile

interface ObjectStorageService {
    fun saveAvatar(file: MultipartFile, ownerUserId: Long): String

}