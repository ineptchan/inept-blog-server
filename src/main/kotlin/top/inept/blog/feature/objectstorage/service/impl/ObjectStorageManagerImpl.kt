package top.inept.blog.feature.objectstorage.service.impl

import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.CommonErrorCode
import top.inept.blog.exception.error.ObjectStorageErrorCode
import top.inept.blog.feature.objectstorage.model.entity.ObjectStorage
import top.inept.blog.feature.objectstorage.model.entity.constraints.ObjectStorageConstraints
import top.inept.blog.feature.objectstorage.repository.ObjectStorageRepository
import top.inept.blog.feature.objectstorage.service.ObjectStorageManager

@Service
class ObjectStorageManagerImpl(
    private val objectStorageRepository: ObjectStorageRepository,
) : ObjectStorageManager {
    override fun saveAndFlushOrThrow(dbObjectStorage: ObjectStorage): ObjectStorage {
        return try {
            objectStorageRepository.saveAndFlush(dbObjectStorage)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                ObjectStorageConstraints.UNIQUE_OBJECT_KEY ->
                    throw BusinessException(ObjectStorageErrorCode.OBJECT_KEY_DB_DUPLICATE, dbObjectStorage.objectKey)

                ObjectStorageConstraints.UNIQUE_SHA_256 ->
                    throw BusinessException(ObjectStorageErrorCode.SHA_256_DB_DUPLICATE, dbObjectStorage.fileHash!!)

                else -> throw BusinessException(CommonErrorCode.UNKNOWN)
            }
        }
    }
}