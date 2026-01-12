package top.inept.blog.feature.comment.model.validated

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@NotBlank(message = "valid.comment.content_cannot_be_empty")
annotation class ValidatedCommentContent(
    val message: String = "valid.common.unknown_error",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
