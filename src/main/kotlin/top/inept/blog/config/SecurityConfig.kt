package top.inept.blog.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import top.inept.blog.feature.admin.user.pojo.entity.enums.UserRole
import top.inept.blog.filter.JwtAuthFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            // 禁用 CSRF，因为我们使用无状态的 JWT
            csrf { disable() }

            authorizeHttpRequests {
                // 定义访问权限
                authorize("/open/**", permitAll)
                authorize("/user/**", hasAuthority(UserRole.USER.authority)) // /user/** 路径需要 USER 角色
                authorize("/admin/**", hasAuthority(UserRole.ADMIN.authority)) // /admin/** 路径需要 ADMIN 角色

                //TODO 发布时关闭
                authorize("/swagger-ui/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)

                authorize(anyRequest, authenticated) // 其他所有请求都需要认证
            }

            // 设置 Session 管理策略为 STATELESS (无状态)
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
        }
        return http.build()
    }
}