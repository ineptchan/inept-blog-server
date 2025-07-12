package top.inept.blog.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import top.inept.blog.base.BaseContext
import top.inept.blog.constant.JwtClaimsConstant
import top.inept.blog.properties.JwtProperties
import top.inept.blog.utils.JwtUtil


@Component
class JwtTokenAdminInterceptor(
    private val jwtProperties: JwtProperties,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (handler !is HandlerMethod) {
            // 当前拦截到的不是动态方法，直接放行
            return true
        }

        try {
            //从请求头中获取令牌
            val token = request.getHeader(jwtProperties.adminTokenName)

            val jwtClaims = JwtUtil.parseJWT(
                secretKey = jwtProperties.adminSecretKey,
                token = token
            )

            if (jwtClaims == null) throw Exception("Invalid JWT token")

            val id = jwtClaims.payload.get(JwtClaimsConstant.ID).toString().toLong()

            BaseContext.setCurrentId(id)
            return true
        } catch (e: Exception) {
            response.status = 401
            return false
        }
    }
}