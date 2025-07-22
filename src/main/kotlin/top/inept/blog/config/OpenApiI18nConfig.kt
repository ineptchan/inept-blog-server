package top.inept.blog.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.MessageSourceAccessor
import top.inept.blog.extensions.get

@Configuration
class OpenApiI18nConfig(private val messages: MessageSourceAccessor) {
    @Bean
    fun i18nCustomizer(): OpenApiCustomizer {
        return OpenApiCustomizer { openApi: OpenAPI ->
            openApi.components?.schemas?.forEach { (_, schema) ->
                translateSchema(schema, messages)
            }
        }
    }

    private fun translateSchema(schema: Schema<*>, source: MessageSourceAccessor) {
        schema.description?.takeIf { it.startsWith("openapi.") }?.also {
            schema.description = source[it]
        }
        schema.properties?.values?.forEach { prop ->
            prop?.let { p ->
                p.description?.takeIf { it.startsWith("openapi.") }?.also {
                    p.description = source[it]
                }
            }
        }
    }
}