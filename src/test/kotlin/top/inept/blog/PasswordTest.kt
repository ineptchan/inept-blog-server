package top.inept.blog

import org.junit.jupiter.api.Test
import top.inept.blog.utils.PasswordUtil

class PasswordTest {

    @Test
    fun passwordEncoder() {
        val string = PasswordUtil.encode("admin123456")
        println(string)
    }
}