package top.inept.blog.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import tools.jackson.databind.ObjectMapper
import top.inept.blog.base.RestBearerAuthEntryPoint

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun restBearerAuthEntryPoint(objectMapper: ObjectMapper, messages: MessageSourceAccessor) =
        RestBearerAuthEntryPoint(objectMapper, messages)

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = listOf(
                "http://localhost:*",
                "http://127.0.0.1:*"
            )
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }

    @Bean
    @Order(1)
    fun publicChain(http: HttpSecurity, environment: Environment): SecurityFilterChain {
        val isProd = environment.acceptsProfiles(Profiles.of("prod"))

        val matchers = buildList {
            add("/public/**")
            add("/auth/**")
            if (!isProd) {
                add("/openapi/**")
            }
        }.toTypedArray()

        http
            .securityMatcher(*matchers)
            .cors { }
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
        restBearerAuthEntryPoint: RestBearerAuthEntryPoint,

        ): SecurityFilterChain {
        http
            .cors { }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint(restBearerAuthEntryPoint)
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.authenticationEntryPoint(restBearerAuthEntryPoint)

                oauth2.jwt { jwt ->
                    jwt.decoder(accessJwtDecoder)
                    jwt.jwtAuthenticationConverter(jwtAuthConverter)
                }
            }

        return http.build()
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