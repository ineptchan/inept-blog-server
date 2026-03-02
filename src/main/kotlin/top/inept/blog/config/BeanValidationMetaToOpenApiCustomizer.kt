package top.inept.blog.config

import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.oas.models.media.Schema
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.stereotype.Component
import java.util.*

@Component
class BeanValidationMetaToOpenApiCustomizer : PropertyCustomizer {
    //by ai
    override fun customize(property: Schema<*>, type: AnnotatedType): Schema<*> {
        val anns = type.ctxAnnotations ?: return property
        val all = flattenAnnotations(anns)

        all.filterIsInstance<Pattern>().firstOrNull()?.let { p ->
            property.pattern = p.regexp
        }
        all.filterIsInstance<Size>().firstOrNull()?.let { s ->
            // Size 用在 String 上时，对应 minLength/maxLength
            property.minLength = s.min
            property.maxLength = s.max
        }

        return property
    }

    private fun flattenAnnotations(direct: Array<Annotation>): List<Annotation> {
        val out = mutableListOf<Annotation>()
        val q = ArrayDeque<Annotation>()
        val seen = mutableSetOf<Class<out Annotation>>()

        direct.forEach { q.add(it) }

        while (q.isNotEmpty()) {
            val a = q.removeFirst()
            val t = a.annotationClass.java
            if (!seen.add(t)) continue
            out += a
            // 把“注解上的注解”(元注解)也加入队列
            t.annotations.forEach { meta ->
                // 过滤掉一些基础元注解可按需加：Target/Retention/Documented 等
                q.add(meta)
            }
        }
        return out
    }
}