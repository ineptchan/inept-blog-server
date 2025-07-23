package top.inept.blog.feature.admin.user.pojo.validated

import jakarta.validation.constraints.Pattern

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Pattern(
    regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]{6,20}$",
    message = "valid.user.password_pattern"
)
annotation class ValidatedPassword