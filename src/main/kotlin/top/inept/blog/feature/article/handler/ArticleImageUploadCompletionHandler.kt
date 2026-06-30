package top.inept.blog.feature.article.handler

import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.apache.tika.Tika
import org.springframework.stereotype.Component
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.ObjectStorageErrorCode
import top.inept.blog.feature.article.model.entity.ArticleObjectStorage
import top.inept.blog.feature.article.repository.ArticleObjectStorageRepository
import top.inept.blog.feature.objectstorage.handler.UploadCompletionHandler
import top.inept.blog.feature.objectstorage.model.entity.ObjectStorage
import top.inept.blog.feature.objectstorage.model.entity.enums.Purpose
import top.inept.blog.feature.objectstorage.model.entity.enums.Status
import top.inept.blog.feature.objectstorage.model.entity.enums.Visibility
import top.inept.blog.feature.objectstorage.model.entity.enums.getBucketName
import top.inept.blog.feature.objectstorage.model.vo.CompleteUploadVO
import top.inept.blog.feature.objectstorage.service.ObjectStorageManager
import top.inept.blog.properties.ObjectStorageProperties
import top.inept.blog.utils.ScrimageUtil
import top.inept.blog.utils.ShaUtil
import java.io.BufferedInputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*

@Component
class ArticleImageUploadCompletionHandler(
    private val mc: MinioClient,
    private val osp: ObjectStorageProperties,
    private val objectStorageManager: ObjectStorageManager,
    private val articleObjectStorageRepository: ArticleObjectStorageRepository,
) : UploadCompletionHandler {
    override fun supports(purpose: Purpose): Boolean {
        return purpose == Purpose.ARTICLE_IMAGE
    }

    override fun handle(
        pendingObjectStorage: ObjectStorage,
        buffered: BufferedInputStream
    ): CompleteUploadVO {
        //获取原始文件的hash
        val originalFileMessageDigest = MessageDigest.getInstance("SHA-256")
        val tikaInputStream = DigestInputStream(buffered, originalFileMessageDigest)

        //读取mime
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

        //判断是不是图片
        if (!mime.startsWith("image/")) {
            throw BusinessException(ObjectStorageErrorCode.NOT_IMAGE_FILE, mime)
        }

        //将图片压缩
        val webpBytes = ScrimageUtil.imageToWebpStream(
            buffered,
            osp.articleImage.quality,
            osp.articleImage.method
        ).use { it.readBytes() }

        val objectKey = "${UUID.randomUUID()}.webp"
        val contentType = "image/webp"
        val objectSize = webpBytes.size.toLong()
        val bucketName = pendingObjectStorage.purpose.getBucketName()

        val newObjectStorage = ObjectStorage(
            ownerUser = pendingObjectStorage.ownerUser,
            objectKey = objectKey,
            purpose = pendingObjectStorage.purpose,
            originalFileName = pendingObjectStorage.originalFileName,
            contentType = contentType,
            fileSize = objectSize,
            bucket = bucketName,
            status = Status.UPLOADED,
            visibility = Visibility.PUBLIC,
            fileHash = ShaUtil.sha256Hex(webpBytes)
        )

        objectStorageManager.saveAndFlushOrThrow(newObjectStorage)

        //保存对象存储与文章关系
        //用pendingObjectStorage获得绑定的article
        val articleObjectStorage = articleObjectStorageRepository.findByObjectStorage_Id(pendingObjectStorage.id)
        if (articleObjectStorage != null) {
            articleObjectStorageRepository.save(
                ArticleObjectStorage(
                    article = articleObjectStorage.article,
                    objectStorage = newObjectStorage
                )
            )
        } else {
            throw BusinessException(ObjectStorageErrorCode.ARTICLE_OBJECT_RELATION_NOT_FOUND, pendingObjectStorage.id)
        }

        //保存原始文件的has256
        pendingObjectStorage.fileHash = ShaUtil.sha256Hex(originalFileMessageDigest)
        objectStorageManager.saveAndFlushOrThrow(pendingObjectStorage)

        //上传webp版本
        webpBytes.inputStream().use {
            val args = PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectKey)
                .stream(it, objectSize, -1)
                .contentType(contentType)
                .build()

            mc.putObject(args)
        }

        val url = "${osp.endpoint}${newObjectStorage.bucket}/${newObjectStorage.objectKey}"

        return CompleteUploadVO(
            id = newObjectStorage.id,
            bucket = newObjectStorage.bucket,
            objectKey = newObjectStorage.objectKey,
            url = url
        )
    }
}