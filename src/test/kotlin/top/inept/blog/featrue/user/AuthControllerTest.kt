package top.inept.blog.featrue.user

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

}