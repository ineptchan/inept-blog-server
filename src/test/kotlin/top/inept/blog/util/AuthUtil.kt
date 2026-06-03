package top.inept.blog.util

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient
import top.inept.blog.feature.auth.model.dto.AuthLoginDTO

object AuthUtil {
    fun loginAndGetRefreshToken(client: RestTestClient, username: String, password: String): Pair<String, String> {
        var accessToken: String? = null

        val res = client
            .post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(AuthLoginDTO(username, password))
            .exchange()
            .expectStatus().isOk
            .expectCookie().exists("X-Refresh-Token")
            .expectCookie().httpOnly("X-Refresh-Token", true)
            .expectBody()
            .jsonPath("$.accessToken").exists()
            .jsonPath("$.accessToken").isNotEmpty
            .jsonPath("$.accessToken").value<String> {
                accessToken = it
            }
            .returnResult()


        return Pair(res.responseCookies["X-Refresh-Token"]!!.first().value, accessToken!!)
    }
}