package top.inept.blog.utils

import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import java.util.*
import javax.crypto.spec.SecretKeySpec

private enum class AccountType(val value: String) {
    Admin("admin"),
    User("user");
}

object JwtUtil {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun createAdminJWT(
        secretKey: String,
        ttlHours: Long,
        claims: Map<String, Any>
    ): String {
        return createJWT(AccountType.Admin.value, secretKey, ttlHours, claims)
    }

    fun createUserJWT(
        secretKey: String,
        ttlHours: Long,
        claims: Map<String, Any>
    ): String {
        return createJWT(AccountType.User.value, secretKey, ttlHours, claims)
    }

    private fun createJWT(
        subject: String,
        secretKey: String,
        ttlHours: Long,
        claims: Map<String, Any>
    ): String {
        val now = Date(System.currentTimeMillis())
        val hmacKey = SecretKeySpec(Base64.getDecoder().decode(secretKey), "HmacSHA512")

        return Jwts.builder()
            .subject(subject)
            .claims(claims)
            .issuedAt(now)
            .expiration(DateUtil.addHoursToDate(now, ttlHours))
            .signWith(hmacKey)
            .compact()
    }

    fun parseAdminJWT(
        secretKey: String,
        token: String
    ): Jws<Claims>? {
        val jwt = parseJWT(secretKey, token)
        if (jwt != null && jwt.payload.subject != AccountType.Admin.value) {
            log.error("token账号类型错误")
            return null
        }
        return jwt
    }

    fun parseUserJWT(
        secretKey: String,
        token: String
    ): Jws<Claims>? {
        val jwt = parseJWT(secretKey, token)
        if (jwt != null && jwt.payload.subject != AccountType.User.value) {
            log.error("token账号类型错误")
            return null
        }
        return jwt
    }

    private fun parseJWT(
        secretKey: String,
        token: String
    ): Jws<Claims>? {
        val hmacKey = SecretKeySpec(Base64.getDecoder().decode(secretKey), "HmacSHA512")

        val claims = try {
            Jwts.parser()
                .verifyWith(hmacKey)
                .build()
                .parseSignedClaims(token)
        } catch (e: ExpiredJwtException) {
            log.error("token过期:", e)
            return null
        } catch (e: JwtException) {
            log.error("token无效:", e)
            return null
        } catch (e: Exception) {
            log.error("token解析失败:", e)
            return null
        }

        return claims
    }
}