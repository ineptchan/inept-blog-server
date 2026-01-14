package top.inept.blog.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
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
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver
import org.springframework.security.web.SecurityFilterChain
import top.inept.blog.properties.JwtProperties
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig() {
    //TODO csrf+httpOnly安全问题
    @Bean
    @Order(1)
    fun publicChain(http: HttpSecurity, environment: Environment): SecurityFilterChain {
        val isDev = environment.acceptsProfiles(Profiles.of("dev"))

        val matchers = buildList {
            add("/public/**")
            add("/auth/**")
            if (isDev) {
                add("/swagger-ui/**")
                add("/v3/api-docs/**")
            }
        }.toTypedArray()

        http
            .securityMatcher(*matchers)
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { it.anyRequest().permitAll() }

        return http.build()
    }

    @Bean
    @Order(2)
    fun apiChain(
        http: HttpSecurity,
        jwtAuthConverter: JwtAuthenticationConverter,
        @Qualifier("accessJwtDecoder") accessJwtDecoder: JwtDecoder,
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .oauth2ResourceServer { oauth2 ->
                oauth2.bearerTokenResolver(HeaderOrCookieBearerTokenResolver("X-Access-Token"))
                oauth2.jwt { jwt ->
                    jwt.decoder(accessJwtDecoder)
                    jwt.jwtAuthenticationConverter(jwtAuthConverter)
                }
            }

        return http.build()
    }

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

    /**
     * 把 JWT 里自定义 claim: auth (List<String>) 映射成 GrantedAuthority
     * 例：["user:read", "user:write"]
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

/**
 * Header or cookie bearer token resolver
 *
 * @property cookieName
 * @constructor Create empty Header or cookie bearer token resolver
 * @author chatgpt
 */
class HeaderOrCookieBearerTokenResolver(
    private val cookieName: String = "X-Access-Token"
) : BearerTokenResolver {

    private val delegate = DefaultBearerTokenResolver()

    override fun resolve(request: HttpServletRequest): String? {
        // 1) 优先走标准 Authorization: Bearer xxx
        val fromHeader = delegate.resolve(request)
        if (!fromHeader.isNullOrBlank()) return fromHeader

        // 2) 再从 Cookie 取
        val raw = request.cookies
            ?.firstOrNull { it.name == cookieName }
            ?.value
            ?: return null

        // 兼容 cookie 里误放了 "Bearer xxx"
        return raw.removePrefix("Bearer ").trim().ifBlank { null }
    }
}