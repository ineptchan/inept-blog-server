package top.inept.blog.feature.objectstorage.service.impl

import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.apache.tika.Tika
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.CommonErrorCode
import top.inept.blog.exception.error.ObjectStorageErrorCode
import top.inept.blog.feature.article.model.dto.UploadArticleFeaturedImageDTO
import top.inept.blog.feature.article.model.dto.UploadArticleImageDTO
import top.inept.blog.feature.article.model.entity.Article
import top.inept.blog.feature.objectstorage.model.entity.ObjectStorage
import top.inept.blog.feature.objectstorage.model.entity.constraints.ObjectStorageConstraints
import top.inept.blog.feature.objectstorage.model.entity.enums.ObjectStorageStatus
import top.inept.blog.feature.objectstorage.model.entity.enums.Purpose
import top.inept.blog.feature.objectstorage.model.entity.enums.Visibility
import top.inept.blog.feature.objectstorage.repository.ObjectStorageRepository
import top.inept.blog.feature.objectstorage.service.ObjectStorageService
import top.inept.blog.properties.ImageProperties
import top.inept.blog.properties.MinioProperties
import top.inept.blog.utils.S3Util
import top.inept.blog.utils.ScrimmageUtil
import top.inept.blog.utils.TikaUtil
import java.io.ByteArrayInputStream
import java.util.*

@Service
class ObjectStorageServiceImpl(
    private val objectStorageRepository: ObjectStorageRepository,
    private val mc: MinioClient,
    private val mp: MinioProperties,
    private val tika: Tika,
    private val ip: ImageProperties,
) : ObjectStorageService {
    private val avatarMaxBytes = 5L * 1024 * 1024
    private val avatarMinSide = 256L
    private val avatarMaxSide = 1024L

    override fun saveAvatar(file: MultipartFile, ownerUserId: Long): String {
        val originalBytes = file.bytes

        //限制上传的文件大小
        if (originalBytes.size.toLong() > avatarMaxBytes) {
            throw BusinessException(
                ObjectStorageErrorCode.AVATAR_FILE_TOO_LARGE,
                originalBytes.size.toLong() / 1024 * 1024
            )
        }

        val parserImageResult = TikaUtil.parserImage(file)

        //限制头像分辨率
        if (parserImageResult.width !in avatarMinSide..avatarMaxSide || parserImageResult.height !in avatarMinSide..avatarMaxSide) {
            throw BusinessException(ObjectStorageErrorCode.AVATAR_RESOLUTION_INVALID)
        }

        //对象名使用uuid生成
        val objectName = UUID.randomUUID().toString().replace("-", "")

        //对象的在s3中的key
        val objectKey = S3Util.buildAvatarPrefix(objectName)

        //上传原始文件
        ByteArrayInputStream(originalBytes).use { bis ->
            val args = PutObjectArgs.builder()
                .bucket(mp.bucket)
                .`object`(S3Util.buildOriginalAvatarPrefix(objectName, parserImageResult.mime))
                .stream(bis, originalBytes.size.toLong(), -1)
                .contentType(parserImageResult.mime)
                .build()
            mc.putObject(args)
        }

        //将图片转WebP格式
        val webpResult = ScrimmageUtil.imageToWebp(originalBytes, ip.avatar.quality, ip.avatar.method)

        // 上传 WebP
        ByteArrayInputStream(webpResult.webpBytes).use { webpStream ->
            val args = PutObjectArgs.builder()
                .bucket(mp.bucket)
                .`object`(objectKey)
                .stream(webpStream, webpResult.webpBytes.size.toLong(), -1)
                .contentType("image/webp")
                .build()

            mc.putObject(args)
        }

        val objectStorage = ObjectStorage(
            ownerUserId = ownerUserId,
            purpose = Purpose.AVATAR,
            originalFileName = file.originalFilename ?: objectName,
            contentType = "image/webp",
            sizeBytes = webpResult.webpBytes.size.toLong(),
            sha256 = webpResult.webpSha256,
            bucket = mp.bucket,
            objectKey = objectKey,
            status = ObjectStorageStatus.UPLOADED,
            visibility = Visibility.PUBLIC
        )

        saveAndFlushOrThrow(objectStorage)

        return S3Util.buildAvatarUrl(mp.endpoint, mp.bucket, objectKey)
    }

    override fun uploadArticleImage(ownerUserId: Long, ownerArticle: Article, dto: UploadArticleImageDTO): String {
        //解析传入的文件
        val parserImageResult = TikaUtil.parserImage(dto.image)

        val originalBytes = dto.image.bytes

        //对象名使用uuid生成
        val objectName = UUID.randomUUID().toString().replace("-", "")

        //对象的在s3中的key
        val objectKey = S3Util.buildArticleImagePrefix(objectName)

        //上传原始文件
        ByteArrayInputStream(originalBytes).use { bis ->
            val args = PutObjectArgs.builder()
                .bucket(mp.bucket)
                .`object`(S3Util.buildOriginalArticleImagePrefix(objectName, parserImageResult.mime))
                .stream(bis, originalBytes.size.toLong(), -1)
                .contentType(parserImageResult.mime)
                .build()
            mc.putObject(args)
        }

        //将图片转WebP格式
        val webpResult = ScrimmageUtil.imageToWebp(originalBytes, ip.articleImage.quality, ip.articleImage.method)

        //上传 WebP
        ByteArrayInputStream(webpResult.webpBytes).use { webpStream ->
            val args = PutObjectArgs.builder()
                .bucket(mp.bucket)
                .`object`(objectKey)
                .stream(webpStream, webpResult.webpBytes.size.toLong(), -1)
                .contentType("image/webp")
                .build()

            mc.putObject(args)
        }

        val objectStorage = ObjectStorage(
            ownerUserId = ownerUserId,
            ownerArticle = ownerArticle,
            purpose = Purpose.ARTICLE_IMAGE,
            originalFileName = dto.image.originalFilename ?: objectName,
            contentType = "image/webp",
            sizeBytes = webpResult.webpBytes.size.toLong(),
            sha256 = webpResult.webpSha256,
            bucket = mp.bucket,
            objectKey = objectKey,
            status = ObjectStorageStatus.UPLOADED,
            visibility = Visibility.PUBLIC
        )

        saveAndFlushOrThrow(objectStorage)

        return S3Util.buildArticleImageUrl(mp.endpoint, mp.bucket, objectKey)
    }

    override fun uploadFeaturedImage(
        ownerUserId: Long,
        ownerArticle: Article,
        dto: UploadArticleFeaturedImageDTO
    ): String {
        //解析传入的文件
        val parserImageResult = TikaUtil.parserImage(dto.featuredImage)

        val originalBytes = dto.featuredImage.bytes

        //对象名使用uuid生成
        val objectName = UUID.randomUUID().toString().replace("-", "")

        //对象的在s3中的key
        val objectKey = S3Util.buildArticleFeaturedImagePrefix(objectName)

        //上传原始文件
        ByteArrayInputStream(originalBytes).use { bis ->
            val args = PutObjectArgs.builder()
                .bucket(mp.bucket)
                .`object`(S3Util.buildOriginalArticleFeaturedImagePrefix(objectName, parserImageResult.mime))
                .stream(bis, originalBytes.size.toLong(), -1)
                .contentType(parserImageResult.mime)
                .build()
            mc.putObject(args)
        }

        //将图片转WebP格式
        val webpResult =
            ScrimmageUtil.imageToWebp(originalBytes, ip.articleFeaturedImage.quality, ip.articleFeaturedImage.method)

        //上传 WebP
        ByteArrayInputStream(webpResult.webpBytes).use { webpStream ->
            val args = PutObjectArgs.builder()
                .bucket(mp.bucket)
                .`object`(objectKey)
                .stream(webpStream, webpResult.webpBytes.size.toLong(), -1)
                .contentType("image/webp")
                .build()

            mc.putObject(args)
        }

        val objectStorage = ObjectStorage(
            ownerUserId = ownerUserId,
            ownerArticle = ownerArticle,
            purpose = Purpose.ARTICLE_FEATURED_IMAGE,
            originalFileName = dto.featuredImage.originalFilename ?: objectName,
            contentType = "image/webp",
            sizeBytes = webpResult.webpBytes.size.toLong(),
            sha256 = webpResult.webpSha256,
            bucket = mp.bucket,
            objectKey = objectKey,
            status = ObjectStorageStatus.UPLOADED,
            visibility = Visibility.PUBLIC
        )

        saveAndFlushOrThrow(objectStorage)

        return S3Util.buildArticleFeaturedImageUrl(mp.endpoint, mp.bucket, objectKey)
    }

    private fun saveAndFlushOrThrow(dbObjectStorage: ObjectStorage): ObjectStorage {
        return try {
            objectStorageRepository.saveAndFlush(dbObjectStorage)
        } catch (e: DataIntegrityViolationException) {
            val violation = e.cause as? ConstraintViolationException
            when (violation?.constraintName) {
                ObjectStorageConstraints.UNIQUE_OBJECT_KEY ->
                    throw BusinessException(ObjectStorageErrorCode.OBJECT_KEY_DB_DUPLICATE, dbObjectStorage.objectKey)

                ObjectStorageConstraints.UNIQUE_SHA_256 ->
                    throw BusinessException(ObjectStorageErrorCode.SHA_256_DB_DUPLICATE, dbObjectStorage.sha256)

                else -> throw BusinessException(CommonErrorCode.UNKNOWN)
            }
        }
    }
}