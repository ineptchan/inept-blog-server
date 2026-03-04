package top.inept.blog.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import top.inept.blog.properties.JwtProperties
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtConfig {
    @Bean("accessJwtSecret")
    fun accessJwtSecret(props: JwtProperties): SecretKey =
        SecretKeySpec(props.accessSecretKey.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")

    @Bean("refreshJwtSecret")
    fun refreshJwtSecret(props: JwtProperties): SecretKey =
        SecretKeySpec(props.refreshSecretKey.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")

    @Bean("accessJwtDecoder")
    fun accessJwtDecoder(@Qualifier("accessJwtSecret") jwtSecret: SecretKey): JwtDecoder =
        NimbusJwtDecoder.withSecretKey(jwtSecret)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()

    @Bean("refreshJwtDecoder")
    fun refreshJwtDecoder(@Qualifier("refreshJwtSecret") jwtSecret: SecretKey): JwtDecoder =
        NimbusJwtDecoder.withSecretKey(jwtSecret)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()

    @Bean("accessJwtEncoder")
    fun accessJwtEncoder(@Qualifier("accessJwtSecret") jwtSecret: SecretKey): JwtEncoder {
        val source = ImmutableSecret<SecurityContext>(jwtSecret.encoded)
        return NimbusJwtEncoder(source)
    }

    @Bean("refreshJwtEncoder")
    fun refreshJwtEncoder(@Qualifier("refreshJwtSecret") jwtSecret: SecretKey): JwtEncoder {
        val source = ImmutableSecret<SecurityContext>(jwtSecret.encoded)
        return NimbusJwtEncoder(source)
    }
}