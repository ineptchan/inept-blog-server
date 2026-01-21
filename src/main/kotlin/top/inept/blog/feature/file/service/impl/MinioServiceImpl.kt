package top.inept.blog.feature.file.service.impl

import io.minio.*
import io.minio.errors.ErrorResponseException
import io.minio.http.Method
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import top.inept.blog.feature.file.model.dto.MinioUploadDTO
import top.inept.blog.feature.file.service.MinioService
import top.inept.blog.properties.MinioProperties
import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class MinioServiceImpl(
    val minioClient: MinioClient,
    val minioProperties: MinioProperties
) : MinioService {
    override fun ensureBucket() {
        val exists = minioClient.bucketExists(
            BucketExistsArgs.builder()
                .bucket(minioProperties.bucket)
                .build()
        )
        if (!exists) {
            minioClient.makeBucket(
                MakeBucketArgs.builder()
                    .bucket(minioProperties.bucket)
                    .build()
            )
        }
    }

    override fun objectExists(objectName: String): Boolean {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(minioProperties.bucket)
                    .`object`(objectName)
                    .build()
            )

            return true
        } catch (e: ErrorResponseException) {
            val code = e.errorResponse().code()
            if ("NoSuchKey" == code) {
                return false
            }

            throw e;
        }
    }


    override fun upload(file: MultipartFile): MinioUploadDTO {
        val objectName = UUID.randomUUID().toString().replace("-", "")
        val bucket = minioProperties.bucket
        ensureBucket()

        val md = MessageDigest.getInstance("SHA-256")

        file.inputStream.use { raw ->
            DigestInputStream(raw, md).use { dis ->
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucket)
                        .`object`(objectName)
                        .contentType(file.contentType ?: "application/octet-stream")
                        // size 已知，partSize 用 -1 让 SDK 自动决定
                        .stream(dis, file.size, -1)
                        .build()
                )
            }
        }

        val sha256Hex = md.digest().joinToString("") { "%02x".format(it) }

        return MinioUploadDTO(
            originalFileName = file.originalFilename ?: "unknown",
            objectName = objectName,
            sha256Hex = sha256Hex,
            mimeType = file.contentType ?: "application/octet-stream",
            bucket = bucket,
            sizeBytes = file.size
        )
    }

    override fun download(objectName: String): InputStream {
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(objectName)
                .build()
        )
    }

    override fun delete(objectName: String) {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(minioProperties.bucket)
                .`object`(objectName)
                .build()
        )
    }

    override fun presignedGetUrl(
        objectName: String,
        duration: Long?,
        unit: TimeUnit
    ): String {
        val builder = GetPresignedObjectUrlArgs.builder()
            .method(Method.GET)
            .bucket(minioProperties.bucket)
            .`object`(objectName)

        duration?.let {
            builder.expiry(it.toInt(), unit)
        }

        return minioClient.getPresignedObjectUrl(builder.build())
    }

    override fun presignedPutUrl(
        objectName: String,
        duration: Long,
        unit: TimeUnit
    ): String {
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.PUT)
                .bucket(minioProperties.bucket)
                .`object`(objectName)
                .expiry(duration.toInt(), unit)
                .build()
        )
    }
}