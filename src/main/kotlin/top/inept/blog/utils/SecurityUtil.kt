package top.inept.blog.utils

import org.springframework.security.core.context.SecurityContext

object SecurityUtil {
    fun parseUsername(securityContext: SecurityContext): String? {
        val authentication = securityContext.authentication
        return if (authentication != null && authentication.isAuthenticated) {
            authentication.name
        } else {
            null
        }
    }
}