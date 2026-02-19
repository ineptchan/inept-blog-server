package top.inept.blog.feature.objectstorage.service.impl

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.webp.WebpWriter
import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.apache.commons.codec.digest.DigestUtils.sha256Hex
import org.apache.tika.Tika
import org.apache.tika.io.TikaInputStream
import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.xml.sax.helpers.DefaultHandler
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.CommonErrorCode
import top.inept.blog.exception.error.ObjectStorageErrorCode
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
import top.inept.blog.utils.TikaUtil
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

@Service
class ObjectStorageServiceImpl(
    private val objectStorageRepository: ObjectStorageRepository,
    private val mc: MinioClient,
    private val mp: MinioProperties,
    private val tika: Tika,
    private val ip: ImageProperties,
) : ObjectStorageService {
    private val maxBytes = 5L * 1024 * 1024
    private val minSide = 256L
    private val maxSide = 1024L

    override fun saveAvatar(file: MultipartFile, ownerUserId: Long): String {
        //对象名使用uuid生成
        val objectName = UUID.randomUUID().toString().replace("-", "")

        //对象的在s3中的keu
        val objectKey = S3Util.buildAvatarPrefix(objectName)

        val originalBytes = file.bytes
        var webpBytesSize: Long = 0
        var webpSha256 = ""

        //限制上传的文件大小
        if (originalBytes.size.toLong() > maxBytes) {
            throw BusinessException(
                ObjectStorageErrorCode.AVATAR_FILE_TOO_LARGE,
                originalBytes.size.toLong() / 1024 * 1024
            )
        }

        //使用tika解析图片
        val parser = AutoDetectParser()
        val metadata = Metadata()
        TikaInputStream.get(originalBytes).use {
            parser.parse(it, DefaultHandler(), metadata, ParseContext())
        }

        //解析文件格式
        val originalMime = metadata.get("Content-Type")
        //不是图片抛出错误
        if (!originalMime.startsWith("image/")) {
            throw BusinessException(ObjectStorageErrorCode.NOT_IMAGE_FILE, originalMime)
        }

        //解析分辨率
        val imageLength = TikaUtil.parseImageLength(metadata)
        val imageWidth = TikaUtil.parseImageWidth(metadata)

        //限制头像分辨率
        if (imageWidth !in minSide..maxSide || imageLength !in minSide..maxSide) {
            throw BusinessException(ObjectStorageErrorCode.AVATAR_RESOLUTION_INVALID)
        }

        //上传原始文件
        ByteArrayInputStream(originalBytes).use { bis ->
            val args = PutObjectArgs.builder()
                .bucket(mp.bucket)
                .`object`(S3Util.buildOriginalAvatarPrefix(objectName, originalMime))
                .stream(bis, originalBytes.size.toLong(), -1)
                .contentType(originalMime)
                .build()
            mc.putObject(args)
        }

        //将图片转WebP格式
        ByteArrayInputStream(originalBytes).use { bis ->
            // 加载图片
            val image = ImmutableImage.loader().fromStream(bis)
            val writer = WebpWriter().withQ(ip.avatar.quality).withM(ip.avatar.method)

            val webpOutputStream = ByteArrayOutputStream()
            image.forWriter(writer).write(webpOutputStream) // 将图片写入输出流
            val webpBytes = webpOutputStream.toByteArray()

            webpBytesSize = webpBytes.size.toLong()
            webpSha256 = sha256Hex(webpBytes)

            // 上传 WebP
            ByteArrayInputStream(webpBytes).use { webpStream ->
                val args = PutObjectArgs.builder()
                    .bucket(mp.bucket)
                    .`object`(objectKey)
                    .stream(webpStream, webpBytes.size.toLong(), -1)
                    .contentType("image/webp")
                    .build()

                mc.putObject(args)
            }
        }

        val objectStorage = ObjectStorage(
            ownerUserId = ownerUserId,
            purpose = Purpose.AVATAR,
            originalFileName = file.originalFilename ?: objectName,
            contentType = "image/webp",
            sizeBytes = webpBytesSize,
            sha256 = webpSha256,
            bucket = mp.bucket,
            objectKey = objectKey,
            status = ObjectStorageStatus.UPLOADED,
            visibility = Visibility.PUBLIC
        )

        saveAndFlushOrThrow(objectStorage)

        return S3Util.buildAvatarUrl(mp.endpoint, mp.bucket, objectKey)
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