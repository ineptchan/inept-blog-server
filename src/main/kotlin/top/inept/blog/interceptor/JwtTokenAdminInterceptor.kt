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

        //从请求头中获取令牌
        val token = request.getHeader(jwtProperties.adminTokenName)

        val jwtClaims = JwtUtil.parseAdminJWT(
            secretKey = jwtProperties.adminSecretKey,
            token = token
        )

        if (jwtClaims != null) {
            val id = jwtClaims.payload.get(JwtClaimsConstant.ADMIN_ID).toString().toLongOrNull()

            if (id != null) {
                BaseContext.setCurrentId(id)
                return true
            }
        }

        response.status = 401
        return false
    }
}