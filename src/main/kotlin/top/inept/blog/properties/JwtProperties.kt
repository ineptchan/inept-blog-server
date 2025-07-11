package top.inept.blog.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "top.inept.jwt")
data class JwtProperties @ConstructorBinding constructor(
    val adminSecretKey: String,
    val adminTtlHours: Long,
    val adminTokenName: String
)