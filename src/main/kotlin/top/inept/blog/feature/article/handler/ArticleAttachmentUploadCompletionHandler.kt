package top.inept.blog.feature.article.handler

import io.minio.CopyObjectArgs
import io.minio.CopySource
import io.minio.MinioClient
import org.apache.tika.Tika
import org.springframework.stereotype.Component
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.ObjectStorageErrorCode
import top.inept.blog.feature.article.model.entity.ArticleObjectStorage
import top.inept.blog.feature.article.repository.ArticleObjectStorageRepository
import top.inept.blog.feature.objectstorage.handler.UploadCompletionHandler
import top.inept.blog.feature.objectstorage.model.entity.ObjectStorage
import top.inept.blog.feature.objectstorage.model.entity.enums.*
import top.inept.blog.feature.objectstorage.service.ObjectStorageManager
import top.inept.blog.properties.ObjectStorageProperties
import top.inept.blog.utils.ShaUtil
import java.io.BufferedInputStream
import java.io.File
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*

@Component
class ArticleAttachmentUploadCompletionHandler(
    private val mc: MinioClient,
    private val osp: ObjectStorageProperties,
    private val objectStorageManager: ObjectStorageManager,
    private val articleObjectStorageRepository: ArticleObjectStorageRepository,
) : UploadCompletionHandler {
    override fun supports(purpose: Purpose): Boolean {
        return purpose == Purpose.ARTICLE_ATTACHMENT
    }

    override fun handle(
        pendingObjectStorage: ObjectStorage,
        buffered: BufferedInputStream
    ): String {
        val originalFileMessageDigest = MessageDigest.getInstance("SHA-256")
        val tikaInputStream = DigestInputStream(buffered, originalFileMessageDigest)
        val mime = Tika().detect(tikaInputStream, pendingObjectStorage.originalFileName)
        buffered.reset()

        //判断之前请求与实际内容是否一致
        if (mime != pendingObjectStorage.contentType) {
            throw BusinessException(
                ObjectStorageErrorCode.CONTENT_TYPE_MISMATCH,
                mime,
                pendingObjectStorage.contentType
            )
        }

        val objectKey = "${UUID.randomUUID()}.${File(pendingObjectStorage.originalFileName).extension}"
        val bucketName = pendingObjectStorage.purpose.getBucketName()
        val objectSize = pendingObjectStorage.fileSize

        //插入对象
        val newObjectStorage = ObjectStorage(
            ownerUser = pendingObjectStorage.ownerUser,
            objectKey = objectKey,
            purpose = pendingObjectStorage.purpose,
            originalFileName = pendingObjectStorage.originalFileName,
            contentType = mime,
            fileSize = objectSize,
            bucket = bucketName,
            status = Status.UPLOADED,
            visibility = Visibility.PUBLIC,
            fileHash = ShaUtil.sha256Hex(originalFileMessageDigest)
        )

        objectStorageManager.saveAndFlushOrThrow(newObjectStorage)

        //保存对象与文章关系
        val articleObjectStorage = articleObjectStorageRepository.findByObjectStorage_Id(pendingObjectStorage.id)
        articleObjectStorage?.let {
            articleObjectStorageRepository.save(
                ArticleObjectStorage(
                    article = it.article,
                    objectStorage = newObjectStorage
                )
            )
        }

        //拷贝对象
        mc.copyObject(
            CopyObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectKey)
                .source(
                    CopySource.builder()
                        .bucket(pendingObjectStorage.purpose.getPendingBucketName())
                        .`object`(pendingObjectStorage.objectKey)
                        .build()
                )
                .build()
        )

        return "${osp.endpoint}${newObjectStorage.bucket}/${newObjectStorage.objectKey}"
    }
}