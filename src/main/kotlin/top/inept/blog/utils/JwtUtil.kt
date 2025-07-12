package top.inept.blog.utils

import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import java.util.*
import javax.crypto.spec.SecretKeySpec

object JwtUtil {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun createJWT(
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

    fun parseJWT(
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