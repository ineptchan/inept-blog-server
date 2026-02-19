package top.inept.blog.config

import io.minio.BucketExistsArgs
import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import top.inept.blog.exception.SystemException
import top.inept.blog.properties.MinioProperties

@Configuration
class MinioConfig {
    @Bean
    fun minioClient(mp: MinioProperties): MinioClient {
        val builder = MinioClient.builder()
            .endpoint(mp.endpoint)
            .credentials(
                mp.accessKey,
                mp.secretKey
            )

        if (mp.region.isNotEmpty()) {
            builder.region(mp.region)
        }

        val mc = builder.build()

        if (!mc.bucketExists(BucketExistsArgs.builder().bucket(mp.bucket).build())) {
            throw SystemException("未创建桶")
        }

        return mc
    }
}