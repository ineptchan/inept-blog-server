package top.inept.blog

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.client.RestTestClient
import top.inept.blog.feature.rbac.repository.RoleRepository
import top.inept.blog.feature.user.service.UserService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
abstract class IntegrationTestBase {

    @Autowired
    lateinit var client: RestTestClient

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var roleRepository: RoleRepository
}