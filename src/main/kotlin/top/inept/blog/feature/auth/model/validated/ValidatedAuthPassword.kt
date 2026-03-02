package top.inept.blog.feature.auth.model.validated

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)\\S+$", message = "valid.auth.password.pattern")
@Size(min = 8, max = 64, message = "valid.auth.password.size")
annotation class ValidatedAuthPassword(
    val message: String = "valid.common.unknown_error",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)