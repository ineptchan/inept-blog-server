package top.inept.blog.feature.auth

import org.junit.jupiter.api.Test
import top.inept.blog.IntegrationTestBase
import top.inept.blog.util.AuthUtil
import kotlin.test.junit5.JUnit5Asserter.fail

class AuthControllerTest : IntegrationTestBase() {

    @Test
    fun `测试登录`() {
        val username = "admintest"
        val pair = AuthUtil.login(httpClient, username, "admin123456")
        if (pair.second.username != username) {
            fail("登录用户不一致")
        }
    }

    @Test
    fun `测试刷新令牌`() {
        val (refreshToken, _) = AuthUtil.login(httpClient, "admintest", "admin123456")

        //刷新令牌
        val pair = AuthUtil.refreshToken(httpClient, refreshToken)
        val newRefreshToken = pair.first

        if (refreshToken == newRefreshToken) {
            fail("RefreshToken没有被轮换")
        }
    }

    @Test
    fun `测试退出登录`() {
        val (refreshToken, _) = AuthUtil.login(httpClient, "admintest", "admin123456")

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

    @Test
    fun `测试RefreshToken被盗`() {
        //用户正常登录
        val (firstRefreshToken, _) = AuthUtil.login(httpClient, "admintest", "admin123456")
        //密钥被盗刷新
        AuthUtil.refreshToken(httpClient, firstRefreshToken)
        //用户未察觉继续用firstRefreshToken刷新
        val res = httpClient.post()
            .uri("/auth/refresh")
            .cookie("X-Refresh-Token", firstRefreshToken)
            .exchange()
            .expectStatus().isUnauthorized
    }
}