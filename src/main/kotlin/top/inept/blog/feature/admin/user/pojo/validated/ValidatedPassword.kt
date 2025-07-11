package top.inept.blog.feature.admin.user.pojo.validated

import jakarta.validation.constraints.Pattern

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Pattern(
    regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]{6,20}$",
    message = "至少一个字母,至少一个数字,长度应为6-20"
)
annotation class ValidatedPassword