package top.inept.blog.feature.auth.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.feature.auth.model.dto.AuthLoginDTO
import top.inept.blog.feature.auth.model.vo.AuthLoginVO
import top.inept.blog.feature.auth.service.AuthService
import java.time.Duration

@Tag(name = "公开身份验证接口")
@RestController
@RequestMapping("/auth")
@Validated
class AuthController(
    private val authService: AuthService,
) {
    @Operation(summary = "登录")
    @PostMapping("/login")
    fun login(@Valid @RequestBody dto: AuthLoginDTO, response: HttpServletResponse): ResponseEntity<AuthLoginVO> {
        val combo = authService.login(dto)

        val refreshToken = combo.value2

        val cookie = ResponseCookie.from("X-Refresh-Token", refreshToken)
            .httpOnly(true)
            .secure(false)          //TODO 必须 HTTPS，本地 http 调试可先 false，上线必须 true
            .sameSite("Lax")
            .path("/auth/refresh")

            //最好从properties算
            .maxAge(Duration.ofDays(7))
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())

        return ResponseEntity.ok(combo.value1)
    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh")
    fun refresh(@CookieValue("X-Refresh-Token") token: String): ResponseEntity<String> {
        val accessToken = authService.refresh(token)
        return ResponseEntity.ok(accessToken)
    }
}