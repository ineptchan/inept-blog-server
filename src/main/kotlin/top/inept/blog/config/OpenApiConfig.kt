package top.inept.blog.config

import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenApiConfig {
    @Bean
    fun publicApi(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("public")
        .pathsToMatch("/public/**")
        .build()

    @Bean
    fun userApi(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("user")
        .pathsToMatch("/user/**")
        .build()

    @Bean
    fun adminApi(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("admin")
        .pathsToMatch("/admin/**")
        .build()
}