package top.inept.blog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import top.inept.blog.properties.JwtProperties

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(JwtProperties::class)
class IneptBlogServerApplication

fun main(args: Array<String>) {
    runApplication<IneptBlogServerApplication>(*args)
}
