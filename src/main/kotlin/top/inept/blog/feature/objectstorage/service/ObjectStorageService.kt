package top.inept.blog.feature.objectstorage.service

import top.inept.blog.feature.objectstorage.model.dto.CompleteUploadDTO
import top.inept.blog.feature.objectstorage.model.dto.PresignUploadDTO
import top.inept.blog.feature.objectstorage.model.vo.PresignUploadVO

interface ObjectStorageService {
    fun presignUpload(dto: PresignUploadDTO): PresignUploadVO
    fun completeUpload(dto: CompleteUploadDTO): String
}