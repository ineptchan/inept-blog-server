package top.inept.blog.feature.user.handler

import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.springframework.stereotype.Component
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.ObjectStorageErrorCode
import top.inept.blog.feature.objectstorage.handler.UploadCompletionHandler
import top.inept.blog.feature.objectstorage.model.entity.ObjectStorage
import top.inept.blog.feature.objectstorage.model.entity.enums.Purpose
import top.inept.blog.feature.objectstorage.model.entity.enums.Status
import top.inept.blog.feature.objectstorage.model.entity.enums.Visibility
import top.inept.blog.feature.objectstorage.model.entity.enums.getBucketName
import top.inept.blog.feature.objectstorage.model.vo.CompleteUploadVO
import top.inept.blog.feature.objectstorage.service.ObjectStorageManager
import top.inept.blog.feature.user.repository.UserRepository
import top.inept.blog.properties.ObjectStorageProperties
import top.inept.blog.utils.ScrimageUtil
import top.inept.blog.utils.ShaUtil
import top.inept.blog.utils.TikaUtil
import java.io.BufferedInputStream
import java.util.*

@Component
class AvatarUploadCompletionHandler(
    private val mc: MinioClient,
    private val osp: ObjectStorageProperties,
    private val userRepository: UserRepository,
    private val objectStorageManager: ObjectStorageManager
) : UploadCompletionHandler {
    override fun supports(purpose: Purpose): Boolean {
        return purpose == Purpose.AVATAR
    }

    override fun handle(
        pendingObjectStorage: ObjectStorage,
        buffered: BufferedInputStream
    ): CompleteUploadVO {
        val originalBytes = buffered.readBytes()

        //解析图片
        val parserImageResult = TikaUtil.parserImage(originalBytes)

        //判断之前请求与实际内容是否一致
        if (parserImageResult.mime != pendingObjectStorage.contentType) {
            throw BusinessException(
                ObjectStorageErrorCode.CONTENT_TYPE_MISMATCH,
                parserImageResult.mime,
                pendingObjectStorage.contentType
            )
        }

        //判断是不是图片
        if (!parserImageResult.mime.startsWith("image/")) {
            throw BusinessException(ObjectStorageErrorCode.NOT_IMAGE_FILE, parserImageResult.mime)
        }

        //限制头像分辨率
        if (parserImageResult.width !in osp.avatar.minSide..osp.avatar.maxSide || parserImageResult.height !in osp.avatar.minSide..osp.avatar.maxSide) {
            throw BusinessException(ObjectStorageErrorCode.AVATAR_RESOLUTION_INVALID)
        }

        val user = pendingObjectStorage.ownerUser

        //将图片压缩
        val webpBytes = ScrimageUtil.imageToWebp(originalBytes, osp.avatar.quality, osp.avatar.method)

        val objectKey = "${user.id}/${UUID.randomUUID()}.webp"
        val contentType = "image/webp"
        val objectSize = webpBytes.size.toLong()
        val bucketName = pendingObjectStorage.purpose.getBucketName()

        val newObjectStorage = ObjectStorage(
            ownerUser = user,
            objectKey = objectKey,
            purpose = pendingObjectStorage.purpose,
            originalFileName = pendingObjectStorage.originalFileName,
            contentType = contentType,
            fileSize = objectSize,
            bucket = bucketName,
            status = Status.UPLOADED,
            visibility = Visibility.PUBLIC,
            fileHash = ShaUtil.sha256Hex(webpBytes),
        )
        objectStorageManager.saveAndFlushOrThrow(newObjectStorage)

        //保存原始文件的has256
        pendingObjectStorage.fileHash = ShaUtil.sha256Hex(originalBytes)
        objectStorageManager.saveAndFlushOrThrow(pendingObjectStorage)

        val avatarUrl = "${osp.endpoint}${newObjectStorage.bucket}/${newObjectStorage.objectKey}"

        //更新头像
        userRepository.updateAvatarById(user.id, avatarUrl)

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

        return CompleteUploadVO(
            id = newObjectStorage.id,
            bucket = newObjectStorage.bucket,
            objectKey = newObjectStorage.objectKey,
            url = avatarUrl
        )
    }
}