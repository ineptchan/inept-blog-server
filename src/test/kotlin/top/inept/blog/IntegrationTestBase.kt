package top.inept.blog

import jakarta.persistence.EntityManager
import net.datafaker.Faker
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.client.RestTestClient
import top.inept.blog.extensions.log
import top.inept.blog.feature.article.repository.ArticleObjectStorageRepository
import top.inept.blog.feature.article.service.ArticleService
import top.inept.blog.feature.auth.model.dto.AuthLoginDTO
import top.inept.blog.feature.auth.model.vo.AuthLoginVO
import top.inept.blog.feature.categories.service.CategoriesService
import top.inept.blog.feature.objectstorage.service.ObjectStorageService
import top.inept.blog.feature.rbac.repository.RoleRepository
import top.inept.blog.feature.rbac.service.PermissionService
import top.inept.blog.feature.rbac.service.RoleService
import top.inept.blog.feature.user.service.UserService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationTestBase {
    val faker = Faker()
    var adminToken: String? = null

    @Autowired
    lateinit var flyway: Flyway

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var httpClient: RestTestClient

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var roleService: RoleService

    @Autowired
    lateinit var permissionService: PermissionService

    @Autowired
    lateinit var objectStorageService: ObjectStorageService

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var articleService: ArticleService

    @Autowired
    lateinit var categoriesService: CategoriesService

    @Autowired
    lateinit var articleObjectStorageRepository: ArticleObjectStorageRepository

    @AfterAll
    fun afterAll() {
        log.info("开始清理数据库")
        flyway.clean()
        flyway.migrate()
        log.info("清理数据库成功")
    }

    @BeforeAll
    fun beforeAll() {
        val result = httpClient.post().uri("/auth/login").body(
            AuthLoginDTO(
                username = "admintest",
                password = "admin123456"
            )
        ).exchange().expectStatus().isOk.expectBody(AuthLoginVO::class.java).returnResult()

        val body = result.responseBody
            ?: fail("登录响应体为空")

        adminToken = body.accessToken
    }

    protected fun mediaResource(path: String): ClassPathResource {
        return ClassPathResource("fixtures/media/$path").also {
            check(it.exists()) {
                "测试资源不存在：fixtures/media/$path"
            }
        }
    }

    protected fun attachmentResource(path: String): ClassPathResource {
        return ClassPathResource("fixtures/attachment/$path").also {
            check(it.exists()) {
                "测试资源不存在：fixtures/attachment/$path"
            }
        }
    }
}