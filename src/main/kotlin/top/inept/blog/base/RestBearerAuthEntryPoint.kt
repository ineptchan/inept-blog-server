package top.inept.blog.base

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.JwtValidationException
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.web.AuthenticationEntryPoint
import tools.jackson.databind.ObjectMapper
import top.inept.blog.extensions.get
import top.inept.blog.utils.ProblemDetailUtil

//处理 没带 token / token 过期 / token 签名错误 / token 格式错误 等认证失败
class RestBearerAuthEntryPoint(
    private val objectMapper: ObjectMapper,
    private val messages: MessageSourceAccessor,
) : AuthenticationEntryPoint {
    private val delegate = BearerTokenAuthenticationEntryPoint()

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        delegate.commence(
            request,
            response,
            authException
        )

        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        val root = rootCause(authException)

        val httpStatus = HttpStatus.valueOf(response.status)

        val (titleKey, detailKey) = when {
            authHeader.isNullOrBlank() -> Pair(
                "message.auth.token_missing",
                "message.auth.token_missing.detail"
            )

            // header 存在但不是 Bearer（更像 invalid_request -> 400）
            authHeader.isNotBlank() && !authHeader.startsWith("Bearer ", ignoreCase = true) ->
                Pair(
                    "message.auth.token_scheme_invalid",
                    "message.auth.token_scheme_invalid.detail"
                )

            //token过期
            root is JwtValidationException && root.errors.any { it.description?.contains("expired", true) == true } ->
                Pair("message.auth.token_expired", "message.auth.token_expired.detail")

            //token签名
            root is BadJwtException && root.message?.contains("signature", true) == true ->
                Pair(
                    "message.auth.token_signature_invalid",
                    "message.auth.token_signature_invalid.detail"
                )

            //token令牌内容无效
            root is JwtException ->
                Pair("message.auth.token_invalid", "message.auth.token_invalid.detail")

            else ->
                Pair("message.auth.unauthorized", "message.auth.unauthorized.detail")
        }

        val problemDetail = ProblemDetailUtil.buildProblemDetail(
            status = httpStatus,
            title = messages[titleKey],
            detail = messages[detailKey],
            props = mapOf("error" to titleKey)
        )


        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()

        objectMapper.writeValue(response.outputStream, problemDetail)
        response.flushBuffer()
    }

    private fun rootCause(ex: Throwable): Throwable {
        var cur: Throwable = ex
        while (cur.cause != null && cur.cause !== cur) cur = cur.cause!!
        return cur
    }
}