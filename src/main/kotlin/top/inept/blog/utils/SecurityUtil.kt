package top.inept.blog.utils

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import top.inept.blog.exception.BusinessException
import top.inept.blog.exception.error.UserErrorCode

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

    fun parsePermissionCodes(securityContext: SecurityContext): Set<String> {
        val authentication = securityContext.authentication

        return if (authentication != null && authentication.isAuthenticated) {
            //跳过authorities里的复杂权限
            authentication.authorities.mapNotNull { it.authority }.toSet()
        } else {
            throw BusinessException(UserErrorCode.PERMISSION_MISSING_CONTEXT)
        }
    }

    fun getClaimAsString(name: String): String? {
        return currentJwt()?.getClaimAsString(name)
    }

    fun getClaim(name: String): Any? {
        return currentJwt()?.claims?.get(name)
    }
}