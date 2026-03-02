package top.inept.blog.feature.auth.model.validated

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@Pattern(regexp = "^[a-zA-Z0-9_]$", message = "valid.auth.username.pattern")
@Size(min = 3, max = 20, message = "valid.auth.username.size")
annotation class ValidateAuthUsername(
    val message: String = "valid.common.unknown_error",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)