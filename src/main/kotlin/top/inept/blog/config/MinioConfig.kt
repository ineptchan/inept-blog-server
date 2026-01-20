package top.inept.blog.config

import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import top.inept.blog.properties.MinioProperties

@Configuration
class MinioConfig {
    @Bean
    fun minioClient(minioProperties: MinioProperties): MinioClient {
        val builder = MinioClient.builder()
            .endpoint(minioProperties.endpoint)
            .credentials(
                minioProperties.accessKey,
                minioProperties.secretKey
            )

        if (minioProperties.region.isNotEmpty()) {
            builder.region(minioProperties.region)
        }

        return builder.build()
    }
}