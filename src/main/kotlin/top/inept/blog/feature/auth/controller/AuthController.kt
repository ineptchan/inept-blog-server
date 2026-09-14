package top.inept.blog.feature.auth.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.feature.auth.model.dto.AuthLoginDTO
import top.inept.blog.feature.auth.model.vo.AuthLoginVO
import top.inept.blog.feature.auth.model.vo.RefreshVO
import top.inept.blog.feature.auth.service.AuthService
import top.inept.blog.properties.JwtProperties
import java.time.Duration

@Tag(name = "身份接口")
@RestController
@RequestMapping("/auth")
@Validated
class AuthController(
    private val authService: AuthService,
    private val jwtProperties: JwtProperties,
    private val environment: Environment
) {
    @Operation(summary = "登录")
    @PostMapping("/login")
    fun login(@Valid @RequestBody dto: AuthLoginDTO, response: HttpServletResponse): ResponseEntity<AuthLoginVO> {
        val loginResult = authService.login(dto)

        //在prod配置模式下必须https才能用携带cookie
        val secure = environment.acceptsProfiles(Profiles.of("prod"))
        response.addRefreshTokenHeader(
            refreshToken = loginResult.refreshToken,
            maxAge = Duration.ofMinutes(jwtProperties.refreshExpiresMinutes),
            secure
        )

        return ResponseEntity.ok(loginResult.dto)
    }

    @Operation(summary = "刷新令牌")
    @SecurityRequirement(name = "refreshToken")
    @PostMapping("/refresh")
    fun refresh(
        @Parameter(hidden = true)
        @CookieValue("X-Refresh-Token")
        token: String,
        response: HttpServletResponse
    ): ResponseEntity<RefreshVO> {
        val refreshResult = authService.refreshToken(token)

        //在prod配置模式下必须https才能用携带cookie
        val secure = environment.acceptsProfiles(Profiles.of("prod"))
        response.addRefreshTokenHeader(
            refreshToken = refreshResult.refreshToken,
            maxAge = Duration.ofMinutes(jwtProperties.refreshExpiresMinutes),
            secure
        )

        return ResponseEntity.ok(RefreshVO(refreshResult.accessToken))
    }

    @Operation(summary = "退出登录")
    @SecurityRequirement(name = "refreshToken")
    @PostMapping("/logout")
    fun logout(
        @Parameter(hidden = true)
        @CookieValue("X-Refresh-Token")
        token: String,
        response: HttpServletResponse
    ): ResponseEntity<String> {
        authService.logout(token)

        //在prod配置模式下必须https才能用携带cookie
        val secure = environment.acceptsProfiles(Profiles.of("prod"))
        response.addRefreshTokenHeader("", Duration.ZERO, secure)

        return ResponseEntity.ok("ok")
    }

    private fun HttpServletResponse.addRefreshTokenHeader(refreshToken: String, maxAge: Duration, secure: Boolean) {
        val cookie = ResponseCookie.from("X-Refresh-Token", refreshToken)
            .httpOnly(true)
            .secure(secure)
            .sameSite("Lax")
            .path("/auth")
            .maxAge(maxAge)
            .build()
        this.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }
}