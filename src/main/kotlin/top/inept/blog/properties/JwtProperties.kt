package top.inept.blog.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "top.inept.jwt")
data class JwtProperties(
    val issuer: String,
    val accessSecretKey: String,
    val refreshSecretKey: String,
    val accessExpiresMinutes: Long,
    val refreshExpiresMinutes: Long,
)