package top.inept.blog.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class MessageConfig : WebMvcConfigurer {
    @Bean
    fun messageAccessor(messageSource: MessageSource): MessageSourceAccessor {
        // 这个 accessor 会自动从 LocaleContextHolder 取 locale
        return MessageSourceAccessor(messageSource)
    }
}