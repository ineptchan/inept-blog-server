package top.inept.blog.utils

import java.security.MessageDigest

object ShaUtil {
    fun sha256Hex(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes) // SHA-256 输出 32 字节
        return digest.joinToString("") { "%02x".format(it) } // 转小写16进制
    }
}