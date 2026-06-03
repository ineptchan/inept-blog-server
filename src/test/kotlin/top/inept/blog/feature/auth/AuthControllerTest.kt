package top.inept.blog.feature.auth

import org.junit.jupiter.api.Test
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import top.inept.blog.IntegrationTestBase
import top.inept.blog.feature.auth.model.dto.AuthLoginDTO


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class AuthControllerTest : IntegrationTestBase() {

    private fun loginAndGetRefreshToken(): String {
        val res = client
            .post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(AuthLoginDTO("admintest", "admin123456"))
            .exchange()
            .expectStatus().isOk
            .expectCookie().exists("X-Refresh-Token")
            .expectCookie().httpOnly("X-Refresh-Token", true)
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.username").isEqualTo("admintest")
            .jsonPath("$.nickname").isEqualTo("inept")
            .jsonPath("$.email").isEqualTo("admin@inept.top")
            .jsonPath("$.accessToken").exists()
            .jsonPath("$.accessToken").isNotEmpty
            .returnResult()

        return res.responseCookies["X-Refresh-Token"]!!.first().value
    }

    @Test
    fun `测试登录`() {
        loginAndGetRefreshToken()
    }

    @Test
    fun `测试刷新令牌`() {
        val refreshToken = loginAndGetRefreshToken()

        client.post()
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
        val refreshToken = loginAndGetRefreshToken()

        client.post().uri("/auth/logout")
            .cookie("X-Refresh-Token", refreshToken)
            .exchange()
            .expectStatus().isOk
            .expectCookie().valueEquals("X-Refresh-Token", "")

        //尝试用退出登录后的令牌刷新应该为401 令牌已被撤销
        client.post().uri("/auth/refresh")
            .cookie("X-Refresh-Token", refreshToken)
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("message.auth.token_has_been_revoked")
    }
}