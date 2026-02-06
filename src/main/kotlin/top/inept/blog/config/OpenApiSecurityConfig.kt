package top.inept.blog.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import org.springframework.context.annotation.Configuration

@SecuritySchemes(
    value = [
        SecurityScheme(
            name = "accessToken",
            type = SecuritySchemeType.HTTP,
            scheme = "bearer",
            bearerFormat = "JWT",
            description = "访问令牌（JWT）"
        ),
        SecurityScheme(
            name = "refreshToken",
            type = SecuritySchemeType.APIKEY,
            `in` = SecuritySchemeIn.COOKIE,
            paramName = "X-Refresh-Token",
            description = "刷新令牌（仅用于刷新接口）"
        )
    ]
)
@Configuration
class OpenApiSecurityConfig