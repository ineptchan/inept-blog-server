package top.inept.blog.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import top.inept.blog.properties.JwtProperties
import top.inept.blog.utils.JwtUtil

@Component
class JwtAuthFilter(
    private val jwtProperties: JwtProperties,
    private val jwtUtil: JwtUtil,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        //判断有没有token
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        //获取token
        val token = authHeader.substring(7)

        //解析token
        val claims = jwtUtil.parseJWT(secretKey = jwtProperties.secretKey, token)

        //获取用户名与role
        val username = jwtUtil.getUsernameFromClaims(claims.payload)
        val role = jwtUtil.getRoleFromClaims(claims.payload)

        if (username == null || role == null) {
            filterChain.doFilter(request, response)
            return
        }

        // 如果上下文中没有认证信息，才进行设置
        if (SecurityContextHolder.getContext().authentication == null) {

            val authToken = UsernamePasswordAuthenticationToken(
                username,
                null,
                listOf<GrantedAuthority>(role)
            )
            authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

            // 将认证对象设置到 SecurityContext 中，表示当前用户已认证
            SecurityContextHolder.getContext().authentication = authToken
        }

        filterChain.doFilter(request, response)
    }
}