package top.inept.blog.feature.rbac

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import top.inept.blog.IntegrationTestBase
import top.inept.blog.util.DTOGeneration

class UserBindRoleTest : IntegrationTestBase() {
    @Test
    fun `用户绑定角色`() {
        val createRoleDTO = DTOGeneration.createRoleDTO()

        //创建角色
        val dbRole = roleService.createRole(createRoleDTO)

        //创建用户
        val createUserDTO = DTOGeneration.createUserDTO(listOf(dbRole.id))

        val dbUser = userService.createUser(
            createUserDTO
        )

        if (dbUser.roleBindings.size != 1) {
            fail("用户绑定的角色数量不正确")
        }

        val filter = dbUser.roleBindings.filter { it.role.code == dbRole.code }
        if (filter.size != 1) {
            fail("预期绑定的角色不一致, 预期code为 ${dbRole.code} 实际code为 ${dbUser.roleBindings.first().role.code}")
        }
    }
}