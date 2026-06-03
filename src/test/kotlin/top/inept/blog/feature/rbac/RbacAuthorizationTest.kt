package top.inept.blog.feature.rbac

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import top.inept.blog.IntegrationTestBase
import top.inept.blog.feature.user.model.dto.CreateUserDTO
import top.inept.blog.feature.user.model.dto.UpdateUserDTO
import top.inept.blog.util.AuthUtil
import java.util.*

class RbacAuthorizationTest : IntegrationTestBase() {
    private var userId: Long? = null
    private var token: String? = null

    @BeforeEach
    fun setUp() {
        val random = UUID.randomUUID().toString().slice(0..5)

        val userRole = roleRepository.findByCode("user")
        if (userRole == null) {
            fail("找不到user角色")
        }

        val user = userService.createUser(
            CreateUserDTO(
                "test$random",
                "test$random",
                "test$random",
                "test$random@inept.top",
                listOf(userRole.id)
            )
        )

        val (refreshToken, accessToken) = AuthUtil.loginAndGetRefreshToken(client, "test$random", "test$random")
        userId = user.id
        token = accessToken
    }

    @AfterEach
    fun tearDown() {
        if (this.userId == null) {
            fail("获取userId失败")
        }

        userService.updateUser(
            userId!!, UpdateUserDTO(null, null, null, null, null, false)
        )
    }

    @Test
    fun `测试rabc权限不足拒绝`() {
        client.get().uri("admin/user")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isForbidden
            .expectBody()
            .jsonPath("$.errorCode").isEqualTo("message.common.authorization_denied")
    }
}