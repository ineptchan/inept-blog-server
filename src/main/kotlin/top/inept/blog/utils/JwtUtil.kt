package top.inept.blog.utils

import io.jsonwebtoken.*
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.stereotype.Component
import top.inept.blog.constant.JwtClaimsConstant
import top.inept.blog.exception.JwtInvalidException
import top.inept.blog.extensions.get
import top.inept.blog.feature.user.model.entity.enums.UserRole
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Component
class JwtUtil(private val messages: MessageSourceAccessor) {
    private fun createJWT(
        secretKey: String,
        ttlHours: Long,
        claims: Map<String, Any>
    ): String {
        val now = Date(System.currentTimeMillis())
        val hmacKey = SecretKeySpec(Base64.getDecoder().decode(secretKey), "HmacSHA512")

        return Jwts.builder()
            .claims(claims)
            .issuedAt(now)
            .expiration(DateUtil.addHoursToDate(now, ttlHours))
            .signWith(hmacKey)
            .compact()
    }

    fun createJWT(
        secretKey: String,
        ttlHours: Long,
        id: Long,
        username: String,
        role: UserRole,
    ): String {
        val payload = HashMap<String, Any>()
        payload.put(JwtClaimsConstant.ID, id)
        payload.put(JwtClaimsConstant.USERNAME, username)
        payload.put(JwtClaimsConstant.ROLE, role.toString())

        return createJWT(secretKey, ttlHours, payload)
    }

    fun parseJWT(
        secretKey: String,
        token: String
    ): Jws<Claims> {
        val hmacKey = SecretKeySpec(Base64.getDecoder().decode(secretKey), "HmacSHA512")

        return try {
            Jwts.parser()
                .verifyWith(hmacKey)
                .build()
                .parseSignedClaims(token)
        } catch (e: ExpiredJwtException) {
            throw JwtInvalidException(messages["message.jwt.expired"])
        } catch (e: JwtException) {
            throw JwtInvalidException(messages["message.jwt.invalid"])
        }
    }

    fun getIdFromClaims(claims: Claims): Long? {
        val raw = claims[JwtClaimsConstant.ID]

        return when (raw) {
            is Number -> raw.toLong()
            is String -> raw.toLongOrNull()
            else -> null
        }
    }

    fun getUsernameFromClaims(claims: Claims): String? {
        val raw = claims[JwtClaimsConstant.USERNAME]
        return raw as? String
    }

    fun getRoleFromClaims(claims: Claims): UserRole? {
        val raw = claims[JwtClaimsConstant.ROLE]
        if (raw !is String) return null

        return try {
            UserRole.valueOf(raw)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}