package top.inept.blog.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
import top.inept.blog.feature.auth.constant.JwtClaimConstants
import top.inept.blog.feature.auth.validation.AuthVersionValidator
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
    fun accessJwtDecoder(
        @Qualifier("accessJwtSecret") jwtSecret: SecretKey,
        props: JwtProperties,
        authVersionValidator: AuthVersionValidator,
    ): JwtDecoder {
        val decoder = NimbusJwtDecoder.withSecretKey(jwtSecret)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()

        val basicValidator = JwtValidators.createDefaultWithIssuer(props.issuer)
        val tokenUseValidator = JwtClaimValidator<String>(
            JwtClaimConstants.TOKEN_USE
        ) { it == JwtClaimConstants.TOKEN_USE_ACCESS }

        decoder.setJwtValidator { jwt ->
            //验证token使用用途
            val basicResult = basicValidator.validate(jwt)

            if (basicResult.hasErrors()) {
                basicResult
            } else {
                //spring默认验证
                val tokenUseResult = tokenUseValidator.validate(jwt)

                if (tokenUseResult.hasErrors()) {
                    tokenUseResult
                } else {
                    //验证authVersion
                    authVersionValidator.validate(jwt)
                }
            }
        }

        return decoder
    }

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