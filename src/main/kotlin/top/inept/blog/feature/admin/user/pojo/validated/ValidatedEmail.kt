package top.inept.blog.feature.admin.user.pojo.validated

import jakarta.validation.constraints.Email

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Email(message = "错误的邮箱")
annotation class ValidatedEmail()
