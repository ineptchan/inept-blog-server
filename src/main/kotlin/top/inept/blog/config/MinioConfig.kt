package top.inept.blog.config

import io.minio.*
import io.minio.messages.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import top.inept.blog.extensions.log
import top.inept.blog.properties.ObjectStorageProperties

@Configuration
class MinioConfig {
    @Bean
    fun minioClient(osp: ObjectStorageProperties): MinioClient {
        val builder = MinioClient.builder()
            .endpoint(osp.endpoint)
            .credentials(
                osp.accessKey,
                osp.secretKey
            )

        if (osp.region.isNotEmpty()) {
            builder.region(osp.region)
        }

        val mc = builder.build()

        osp.buckets.forEach { bucket ->
            if (!mc.bucketExists(BucketExistsArgs.builder().bucket(bucket.name).build())) {
                mc.makeBucket(MakeBucketArgs.builder().bucket(bucket.name).build())
                log.info("创建 ${bucket.name} 桶成功")

                if (bucket.publicRead) {
                    val policy =
                        """{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["*"]},"Action":["s3:GetObject"],"Resource":["arn:aws:s3:::${bucket.name}/*"]}]} """.trimIndent()

                    mc.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                            .bucket(bucket.name)
                            .config(policy)
                            .build()
                    )

                    log.info("${bucket.name} 桶设置公开访问成功")

                    if (bucket.createPending) {
                        val pendingBucketName = "${bucket.name}-pending"
                        if (!mc.bucketExists(BucketExistsArgs.builder().bucket(pendingBucketName).build())) {
                            mc.makeBucket(MakeBucketArgs.builder().bucket(pendingBucketName).build())
                            log.info("创建 $pendingBucketName 桶成功")
                        }
                    }
                }

                bucket.pendingRetention?.let { pendingRetention ->
                    val rule = LifecycleRule(
                        Status.ENABLED,
                        null,
                        Expiration(null as ResponseDate?, pendingRetention.toDays().toInt(), null),
                        RuleFilter("/"),
                        "delete-tmp-after-${pendingRetention.toDays()}-days",
                        null,
                        null,
                        null,
                    )

                    val config = LifecycleConfiguration(listOf(rule))

                    mc.setBucketLifecycle(
                        SetBucketLifecycleArgs.builder()
                            .bucket(bucket.name)
                            .config(config)
                            .build()
                    )
                }
            }
        }
        return mc
    }
}