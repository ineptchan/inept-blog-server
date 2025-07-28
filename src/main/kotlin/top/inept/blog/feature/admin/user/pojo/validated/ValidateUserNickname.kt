package top.inept.blog.feature.admin.user.pojo.validated

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@Size(min = 2, max = 16, message = "valid.user.nickname")
annotation class ValidateUserNickname(
    val message: String = "valid.common.unknown_error",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)