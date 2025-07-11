package top.inept.blog.utils

import org.springframework.util.DigestUtils

object PasswordUtil {
    fun formatPassword(password: String): String{
        return DigestUtils.md5DigestAsHex(password.toByteArray())
    }
}