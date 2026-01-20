package top.inept.blog.feature.file.service.impl

import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import top.inept.blog.feature.file.model.entity.FileStorage
import top.inept.blog.feature.file.model.entity.constraints.FileStorageConstraints
import top.inept.blog.feature.file.model.entity.enums.FileStorageStatus
import top.inept.blog.feature.file.repository.FileStorageRepository
import top.inept.blog.feature.file.service.FileStorageService
import top.inept.blog.feature.file.service.MinioService
import java.time.Instant
import java.util.concurrent.TimeUnit

@Service
class FileStorageServiceImpl(
    private val minioService: MinioService,
    private val fileStorageRepository: FileStorageRepository,
) : FileStorageService {
    //TODO 异常修复

    override fun upload(file: MultipartFile): String {
        val dto = minioService.upload(file)

        //确认有没有上传成功
        if (!minioService.objectExists(dto.objectName)) {
            throw Exception("minio上传失败")
        }

        val dbFileStorage = FileStorage(
            objectName = dto.objectName,
            mimeType = dto.mimeType,
            originalFileName = dto.originalFileName,
            sizeBytes = dto.sizeBytes,
            sha256 = dto.sha256Hex,
            status = FileStorageStatus.UPLOADED,
            bucket = dto.bucket,
        )

        try {
            fileStorageRepository.saveAndFlush(dbFileStorage)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                FileStorageConstraints.UNIQUE_OBJECT_NAME -> throw Exception("minio object 重复")
                FileStorageConstraints.UNIQUE_SHA_256 -> throw Exception("sha256 重复")
            }
        }

        return minioService.presignedGetUrl(dto.objectName, 10, TimeUnit.MINUTES)
    }

    override fun presignedGetUrl(id: Long): String {
        val fileStorage = (fileStorageRepository.findByIdOrNull(id)
            ?: throw Exception("未找到"))

        if (fileStorage.status == FileStorageStatus.DELETED || fileStorage.deletedAt != null) {
            throw Exception("已经删除")
        }

        return minioService.presignedGetUrl(fileStorage.objectName, 10, TimeUnit.MINUTES)
    }

    override fun presignedGetUrl(objectName: String): String {
        val fileStorage = (fileStorageRepository.findFileStorageByOriginalFileName(objectName)
            ?: throw Exception("未找到"))

        if (fileStorage.status == FileStorageStatus.DELETED || fileStorage.deletedAt != null) {
            throw Exception("已经删除")
        }

        return minioService.presignedGetUrl(fileStorage.objectName, 10, TimeUnit.MINUTES)
    }

    override fun delete(id: Long) {
        val fileStorage = (fileStorageRepository.findByIdOrNull(id)
            ?: throw Exception("未找到"))

        fileStorage.deletedAt = Instant.now()
        fileStorage.status = FileStorageStatus.DELETED

        fileStorageRepository.saveAndFlush(fileStorage)
    }


}