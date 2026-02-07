package top.inept.blog.feature.article.model.validated

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@NotBlank(message = "valid.article.title.notblank")
@Size(max = 24, message = "valid.article.title.size")
annotation class ValidatedArticleTitle(
    val message: String = "valid.common.unknown_error",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)