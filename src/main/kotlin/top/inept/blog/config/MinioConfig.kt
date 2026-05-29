package top.inept.blog.config

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.SetBucketPolicyArgs
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import top.inept.blog.extensions.log
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
            log.info("未创建桶")
            mc.makeBucket(MakeBucketArgs.builder().bucket(mp.bucket).build())

            val policy = """
            {
              "Version": "2012-10-17",
              "Statement": [
                {
                  "Effect": "Allow",
                  "Principal": {
                    "AWS": ["*"]
                  },
                  "Action": [
                    "s3:GetObject"
                  ],
                  "Resource": [
                    "arn:aws:s3:::${mp.bucket}/*"
                  ]
                }
              ]
            }
        """.trimIndent()

            mc.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                    .bucket(mp.bucket)
                    .config(policy)
                    .build()
            )

            log.info("创建桶成功")
        }

        return mc
    }
}