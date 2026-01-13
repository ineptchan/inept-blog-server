package top.inept.blog.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import top.inept.blog.properties.JwtProperties
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig() {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        environment: Environment,
        jwtAuthConverter: JwtAuthenticationConverter
    ): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/public/**"
                ).permitAll()

                //非prod配置开启openapi
                val notProd: Boolean = environment.acceptsProfiles(Profiles.of("!prod"))
                if (notProd) {
                    it.requestMatchers("/swagger-ui/**").permitAll()
                    it.requestMatchers("/v3/api-docs/**").permitAll()
                }

                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthConverter)
                }
            }
            .build()
    }

    /** 用 HS256 对称密钥做 JwtEncoder/JwtDecoder */
    @Bean
    fun jwtSecret(jwtProperties: JwtProperties): SecretKey {
        return SecretKeySpec(jwtProperties.secretKey.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
    }

    @Bean
    fun jwtDecoder(jwtSecret: SecretKey): JwtDecoder {
        return NimbusJwtDecoder.withSecretKey(jwtSecret)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()
    }

    @Bean
    fun jwtEncoder(jwtSecret: SecretKey): JwtEncoder {
        val source = ImmutableSecret<SecurityContext>(jwtSecret.encoded)
        return NimbusJwtEncoder(source)
    }

    /**
     * 把 JWT 里自定义 claim: auth (List<String>) 映射成 GrantedAuthority
     * 例：["ROLE_ADMIN", "user:read", "user:write"]
     */
    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()

        converter.setJwtGrantedAuthoritiesConverter { jwt ->
            val raw = jwt.claims["auth"]
            if (raw !is Collection<*>) return@setJwtGrantedAuthoritiesConverter emptySet()

            raw.asSequence()
                .filterNotNull()
                .map { it.toString() }
                .map { SimpleGrantedAuthority(it) }
                .toSet()
        }

        return converter
    }
}