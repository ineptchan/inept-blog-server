package top.inept.blog.feature.objectstorage.service.impl

import io.minio.GetObjectArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.RemoveObjectArgs
import io.minio.http.Method
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.ArticleErrorCode
import top.inept.blog.exception.error.CommonErrorCode
import top.inept.blog.exception.error.ObjectStorageErrorCode
import top.inept.blog.exception.error.UserErrorCode
import top.inept.blog.feature.article.model.entity.Article
import top.inept.blog.feature.article.model.entity.ArticleObjectStorage
import top.inept.blog.feature.article.repository.ArticleObjectStorageRepository
import top.inept.blog.feature.article.repository.ArticleRepository
import top.inept.blog.feature.auth.constant.JwtClaimConstants
import top.inept.blog.feature.objectstorage.handler.UploadCompletionHandler
import top.inept.blog.feature.objectstorage.model.dto.CompleteUploadDTO
import top.inept.blog.feature.objectstorage.model.dto.PresignUploadDTO
import top.inept.blog.feature.objectstorage.model.entity.ObjectStorage
import top.inept.blog.feature.objectstorage.model.entity.enums.*
import top.inept.blog.feature.objectstorage.model.vo.PresignUploadVO
import top.inept.blog.feature.objectstorage.repository.ObjectStorageRepository
import top.inept.blog.feature.objectstorage.service.ObjectStorageManager
import top.inept.blog.feature.objectstorage.service.ObjectStorageService
import top.inept.blog.feature.user.model.entity.User
import top.inept.blog.properties.ObjectStorageProperties
import top.inept.blog.utils.SecurityUtil
import java.io.File
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class ObjectStorageServiceImpl(
    private val objectStorageRepository: ObjectStorageRepository,
    private val mc: MinioClient,
    private val osp: ObjectStorageProperties,
    private val entityManager: EntityManager,
    private val objectStorageManager: ObjectStorageManager,
    private val handlers: List<UploadCompletionHandler>,
    private val articleRepository: ArticleRepository,
    private val articleObjectStorageRepository: ArticleObjectStorageRepository,
) : ObjectStorageService {
    override fun presignUpload(dto: PresignUploadDTO): PresignUploadVO {
        val maxSize = dto.purpose.getMaxSize(osp)
        //判断内容大小
        if (dto.fileSize > maxSize) {
            throw BusinessException(ObjectStorageErrorCode.CONTENT_SIZE_EXCEEDED, dto.fileSize, maxSize)
        }

        if (dto.purpose in listOf(
                Purpose.ARTICLE_IMAGE,
                Purpose.ARTICLE_FEATURED_IMAGE,
                Purpose.ARTICLE_VIDEO,
                Purpose.ARTICLE_ATTACHMENT
            ) && dto.articleId == null
        ) {
            throw BusinessException(ObjectStorageErrorCode.ARTICLE_ID_REQUIRED, dto.purpose)
        }

        //判断文章在数据库是否存在
        dto.articleId?.let { articleId ->
            if (!articleRepository.existsById(articleId)) {
                throw BusinessException(ArticleErrorCode.ID_NOT_FOUND, articleId)
            }
        }

        val objectKey = "${UUID.randomUUID()}.${File(dto.fileName).extension}"
        val bucketName = dto.purpose.getPendingBucketName()
        val method = Method.PUT
        val duration = 5
        val unit = TimeUnit.MINUTES
        val now = LocalDateTime.now()

        val uploadUrl = mc.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(method)
                .bucket(bucketName)
                .`object`(objectKey)
                .expiry(duration, unit)
                .extraHeaders(mapOf("Content-Type" to dto.contentType))
                .build()
        )

        val userId = SecurityUtil.currentJwt()?.getClaimAsString(JwtClaimConstants.USER_ID)?.toLongOrNull()
            ?: throw BusinessException(UserErrorCode.USER_ID_MISSING_CONTEXT)

        val objectStorage = ObjectStorage(
            ownerUser = entityManager.getReference(User::class.java, userId),
            objectKey = objectKey,
            purpose = dto.purpose,
            originalFileName = dto.fileName,
            contentType = dto.contentType,
            fileSize = dto.fileSize,
            bucket = bucketName,
            status = Status.PREPARED,
            visibility = Visibility.PRIVATE,
        )

        objectStorageManager.saveAndFlushOrThrow(objectStorage)

        val expiresInSeconds = unit.toSeconds(duration.toLong())

        dto.articleId?.let { articleId ->
            val articleObjectStorage = ArticleObjectStorage(
                article = entityManager.getReference(Article::class.java, articleId),
                objectStorage = objectStorage,
            )
            articleObjectStorageRepository.save(articleObjectStorage)
        }

        return PresignUploadVO(
            id = objectStorage.id,
            bucket = bucketName,
            objectKey = objectKey,
            method = method.name,
            url = uploadUrl,
            expiresInSeconds = expiresInSeconds,
            expiresAt = now.plusSeconds(expiresInSeconds)
        )
    }

    @Transactional
    override fun completeUpload(dto: CompleteUploadDTO): String {
        val pendingObjectStorage = (objectStorageRepository.findByIdOrNull(dto.id)
            ?: throw BusinessException(ObjectStorageErrorCode.ID_NOT_FOUND, dto.id))

        //判断防止重放
        if (pendingObjectStorage.status !in setOf(Status.PREPARED, Status.UPLOADING)) {
            throw BusinessException(ObjectStorageErrorCode.INVALID_UPLOAD_STATUS, pendingObjectStorage.status)
        }

        //判断预签名与完成上传是不是一个用户
        val userId = SecurityUtil.currentJwt()?.getClaimAsString(JwtClaimConstants.USER_ID)?.toLongOrNull()
            ?: throw BusinessException(UserErrorCode.USER_ID_MISSING_CONTEXT)
        if (pendingObjectStorage.ownerUser.id != userId) {
            throw BusinessException(ObjectStorageErrorCode.UPLOAD_OWNER_MISMATCH)
        }

        val url = try {
            mc.getObject(
                GetObjectArgs.builder()
                    .bucket(pendingObjectStorage.bucket)
                    .`object`(pendingObjectStorage.objectKey)
                    .build()
            ).use { response ->
                //判断对象大小
                val objectSize = response.headers().get("Content-Length")
                    ?.toLongOrNull()
                    ?: throw BusinessException(CommonErrorCode.REQUIRED_DATA_MISSING, "Content-Length")

                //大小与预签名的请求是否一致
                if (objectSize != pendingObjectStorage.fileSize) {
                    throw BusinessException(
                        ObjectStorageErrorCode.OBJECT_SIZE_MISMATCH,
                        objectSize,
                        pendingObjectStorage.fileSize
                    )
                }

                //是否在规定范围内
                val maxSize = pendingObjectStorage.purpose.getMaxSize(osp)
                if (objectSize !in 0..maxSize) {
                    throw BusinessException(ObjectStorageErrorCode.OBJECT_SIZE_INVALID, objectSize, maxSize)
                }

                //类型与预签名的请求是否一致
                val contentType = response.headers().get("Content-Type")
                    ?: throw BusinessException(CommonErrorCode.REQUIRED_DATA_MISSING, "Content-Type")

                if (contentType != pendingObjectStorage.contentType) {
                    throw BusinessException(
                        ObjectStorageErrorCode.CONTENT_TYPE_MISMATCH,
                        contentType,
                        pendingObjectStorage.contentType
                    )
                }

                //开始实际的处理
                val handler = handlers.singleOrNull {
                    it.supports(pendingObjectStorage.purpose)
                } ?: throw BusinessException(
                    ObjectStorageErrorCode.UNKNOWN_PURPOSE,
                    pendingObjectStorage.purpose
                )

                handler.handle(pendingObjectStorage, response.buffered(bufferSize = 64 * 1024))
            }
        } catch (e: Exception) {
            throw e
        } finally {
            //将Pending版本删除
            mc.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(pendingObjectStorage.bucket)
                    .`object`(pendingObjectStorage.objectKey)
                    .build()
            )

            //更新数据库状态
            pendingObjectStorage.status = Status.DELETED
            objectStorageManager.saveAndFlushOrThrow(pendingObjectStorage)
        }

        return url
    }
}