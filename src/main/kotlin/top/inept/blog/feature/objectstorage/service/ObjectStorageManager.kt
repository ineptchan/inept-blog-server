package top.inept.blog.feature.objectstorage.service

import top.inept.blog.feature.objectstorage.model.entity.ObjectStorage

interface ObjectStorageManager {
    fun saveAndFlushOrThrow(dbObjectStorage: ObjectStorage): ObjectStorage
}