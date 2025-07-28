package top.inept.blog.utils

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object PasswordUtil {
    fun encode(password: String) = BCryptPasswordEncoder().encode(password)
}