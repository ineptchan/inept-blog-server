package top.inept.blog.properties

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "top.inept.image")
@Validated
data class ImageProperties(
    @field:Valid
    val avatar: Avatar
)

data class Avatar(
    @field:Max(100)
    @field:Min(0)
    val quality: Int,

    @field:Max(6)
    @field:Min(0)
    val method: Int,
)