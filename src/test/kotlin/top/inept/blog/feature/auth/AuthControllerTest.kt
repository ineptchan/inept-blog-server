package top.inept.blog.feature.auth

import org.junit.jupiter.api.Test
import top.inept.blog.IntegrationTestBase
import top.inept.blog.util.AuthUtil

class AuthControllerTest : IntegrationTestBase() {

    @Test
    fun `测试登录`() {
        AuthUtil.loginAndGetRefreshToken(httpClient, "admintest", "admin123456")
    }

    @Test
    fun `测试刷新令牌`() {
        val (refreshToken, _) = AuthUtil.loginAndGetRefreshToken(httpClient, "admintest", "admin123456")

        httpClient.post()
            .uri("/auth/refresh")
            .cookie("X-Refresh-Token", refreshToken)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.accessToken").exists()
            .jsonPath("$.accessToken").isNotEmpty
    }

    @Test
    fun `测试退出登录`() {
        val (refreshToken, _) = AuthUtil.loginAndGetRefreshToken(httpClient, "admintest", "admin123456")

        httpClient.post().uri("/auth/logout")
            .cookie("X-Refresh-Token", refreshToken)
            .exchange()
            .expectStatus().isOk
            .expectCookie().valueEquals("X-Refresh-Token", "")

        //尝试用退出登录后的令牌刷新应该为401 令牌已被撤销
        httpClient.post().uri("/auth/refresh")
            .cookie("X-Refresh-Token", refreshToken)
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("message.auth.token_has_been_revoked")
    }
}