package top.inept.blog.util

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody
import top.inept.blog.feature.auth.model.dto.AuthLoginDTO
import top.inept.blog.feature.auth.model.vo.AuthLoginVO
import top.inept.blog.feature.auth.model.vo.RefreshVO
import kotlin.test.junit5.JUnit5Asserter.fail

object AuthUtil {
    fun login(client: RestTestClient, username: String, password: String): Pair<String, AuthLoginVO> {
        val res = client
            .post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(AuthLoginDTO(username, password))
            .exchange()
            .expectStatus().isOk
            .expectCookie().exists("X-Refresh-Token")
            .expectCookie().httpOnly("X-Refresh-Token", true)
            .expectBody<AuthLoginVO>()
            .returnResult()

        val refreshToken = res.responseCookies["X-Refresh-Token"]!!.first().value
        val loginVO = res.responseBody
            ?: fail("返回响应体为空")

        return Pair(refreshToken, loginVO)
    }

    fun refreshToken(client: RestTestClient, refreshToken: String): Pair<String, RefreshVO> {
        val res = client.post()
            .uri("/auth/refresh")
            .cookie("X-Refresh-Token", refreshToken)
            .exchange()
            .expectStatus().isOk
            .expectBody<RefreshVO>()
            .returnResult()

        val refreshToken = res.responseCookies["X-Refresh-Token"]!!.first().value
        val refreshVO = (res.responseBody
            ?: fail("返回响应体为空"))

        return Pair(refreshToken, refreshVO)
    }
}