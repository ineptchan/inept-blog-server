package top.inept.blog.utils

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

object SecurityUtil {
    fun parseUsername(securityContext: SecurityContext): String? {
        val authentication = securityContext.authentication
        return if (authentication != null && authentication.isAuthenticated) {
            authentication.name
        } else {
            null
        }
    }


    fun currentJwt(): Jwt? {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication?.principal as? Jwt
    }

    fun getClaimAsString(name: String): String? {
        return currentJwt()?.getClaimAsString(name)
    }

    fun getClaim(name: String): Any? {
        return currentJwt()?.claims?.get(name)
    }
}