package top.inept.blog.feature.file.service.impl

import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.CommonErrorCode
import top.inept.blog.exception.error.FileErrorCode
import top.inept.blog.feature.file.model.entity.FileStorage
import top.inept.blog.feature.file.model.entity.constraints.FileStorageConstraints
import top.inept.blog.feature.file.model.entity.enums.FileStorageStatus
import top.inept.blog.feature.file.model.vo.FileStorageVO
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
    override fun upload(file: MultipartFile): FileStorageVO {
        val dto = minioService.upload(file)

        if (!minioService.objectExists(dto.objectName)) {
            throw BusinessException(FileErrorCode.UPLOAD_FAILED, dto.objectName)
        }

        val savedFileStorage = saveAndFlushFileStorageOrThrow(
            FileStorage(
                objectName = dto.objectName,
                mimeType = dto.mimeType,
                originalFileName = dto.originalFileName,
                sizeBytes = dto.sizeBytes,
                sha256 = dto.sha256Hex,
                status = FileStorageStatus.UPLOADED,
                bucket = dto.bucket,
            )
        )

        val url = minioService.presignedGetUrl(savedFileStorage.objectName, null, TimeUnit.MINUTES)

        return FileStorageVO(
            id = savedFileStorage.id,
            url = url,
            objectName = savedFileStorage.objectName,
        )
    }

    override fun presignedGetUrl(id: Long): FileStorageVO {
        val dbFileStorage = getFileStorageByIdOrThrow(id)
        ensureNotDeleted(dbFileStorage)

        val url = minioService.presignedGetUrl(dbFileStorage.objectName, 10, TimeUnit.MINUTES)

        return FileStorageVO(
            id = dbFileStorage.id,
            url = url,
            objectName = dbFileStorage.objectName,
        )
    }

    override fun presignedGetUrl(objectName: String): FileStorageVO {
        val dbFileStorage = fileStorageRepository.findFileStorageByOriginalFileName(objectName)
            ?: throw BusinessException(FileErrorCode.OBJECT_NAME_NOT_FOUND, objectName)

        ensureNotDeleted(dbFileStorage)

        val url = minioService.presignedGetUrl(dbFileStorage.objectName, 10, TimeUnit.MINUTES)

        return FileStorageVO(
            id = dbFileStorage.id,
            url = url,
            objectName = dbFileStorage.objectName,
        )
    }

    override fun delete(id: Long) {
        val fileStorage = getFileStorageByIdOrThrow(id)
        ensureNotDeleted(fileStorage)

        fileStorage.deletedAt = Instant.now()
        fileStorage.status = FileStorageStatus.DELETED

        fileStorageRepository.saveAndFlush(fileStorage)
    }

    private fun getFileStorageByIdOrThrow(id: Long): FileStorage {
        return fileStorageRepository.findByIdOrNull(id)
            ?: throw BusinessException(FileErrorCode.ID_NOT_FOUND, id)
    }

    private fun ensureNotDeleted(fileStorage: FileStorage) {
        if (fileStorage.status == FileStorageStatus.DELETED || fileStorage.deletedAt != null) {
            throw BusinessException(FileErrorCode.FILE_ALREADY_DELETED, fileStorage.id)
        }
    }

    private fun saveAndFlushFileStorageOrThrow(fileStorage: FileStorage): FileStorage {
        return try {
            fileStorageRepository.saveAndFlush(fileStorage)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                FileStorageConstraints.UNIQUE_OBJECT_NAME ->
                    throw BusinessException(FileErrorCode.OBJECT_NAME_DB_DUPLICATE, fileStorage.objectName)

                FileStorageConstraints.UNIQUE_SHA_256 ->
                    throw BusinessException(FileErrorCode.SHA256_DB_DUPLICATE, fileStorage.sha256)

                else -> throw BusinessException(CommonErrorCode.UNKNOWN)
            }
        }
    }
}
