package top.inept.blog.feature.admin.user.pojo.validated

import jakarta.validation.constraints.Pattern


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Pattern(regexp = "^[a-zA-Z0-9_]{6,16}$", message = "user.username_pattern")
annotation class ValidUsername
