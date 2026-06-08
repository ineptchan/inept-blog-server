package top.inept.blog

import jakarta.persistence.EntityManager
import net.datafaker.Faker
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.client.RestTestClient
import top.inept.blog.feature.rbac.repository.RoleRepository
import top.inept.blog.feature.rbac.service.PermissionService
import top.inept.blog.feature.rbac.service.RoleService
import top.inept.blog.feature.user.service.UserService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationTestBase {
    val faker = Faker()

    @Autowired
    lateinit var flyway: Flyway

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var client: RestTestClient

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var roleService: RoleService

    @Autowired
    lateinit var permissionService: PermissionService

    @Autowired
    lateinit var roleRepository: RoleRepository

    @AfterAll
    fun afterAll() {
        flyway.clean()
        flyway.migrate()
    }
}