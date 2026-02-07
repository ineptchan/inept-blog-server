package top.inept.blog.feature.user.model.validated

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@Size(min = 2, max = 16, message = "valid.user.nickname.size")
@Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_\\-]+$", message = "valid.user.nickname.pattern")
annotation class ValidateUserNickname(
    val message: String = "valid.common.unknown_error",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)