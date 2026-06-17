package top.inept.blog.properties

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.hibernate.validator.constraints.time.DurationMin
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "top.inept.object-storage")
data class ObjectStorageProperties(
    val endpoint: String,
    val accessKey: String,
    val secretKey: String,
    val region: String,
    val buckets: List<BucketProperties> = emptyList(),

    @field:Valid
    val avatar: Image,

    @field:Valid
    val articleImage: Image,

    @field:Valid
    val articleFeaturedImage: Image,

    @field:Valid
    val articleVideo: Video,

    @field:Valid
    val articleAttachment: File
) {
    data class BucketProperties(
        var name: String = "",
        var publicRead: Boolean = false,
        var createPending: Boolean = false,

        @field:DurationMin(days = 1)
        var pendingRetention: Duration? = null
    )

    data class Image(
        @field:Max(100)
        @field:Min(0)
        val quality: Int,

        @field:Max(6)
        @field:Min(0)
        val method: Int,

        @field:Min(1)
        val maxFileSize: Long,

        val maxSide: Long,

        val minSide: Long,
    )

    data class Video(
        @field:Min(1)
        val maxFileSize: Long,

        val isCompress: Boolean,
    )

    data class File(
        @field:Min(1)
        val maxFileSize: Long,
    )
}