package top.inept.blog.feature.objectstorage.handler

import top.inept.blog.feature.objectstorage.model.entity.ObjectStorage
import top.inept.blog.feature.objectstorage.model.entity.enums.Purpose
import top.inept.blog.feature.objectstorage.model.vo.CompleteUploadVO
import java.io.BufferedInputStream

interface UploadCompletionHandler {

    fun supports(purpose: Purpose): Boolean

    fun handle(
        pendingObjectStorage: ObjectStorage,
        buffered: BufferedInputStream
    ): CompleteUploadVO
}