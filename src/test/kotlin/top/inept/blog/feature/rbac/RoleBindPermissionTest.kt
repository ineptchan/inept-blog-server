package top.inept.blog.feature.rbac

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import top.inept.blog.IntegrationTestBase
import top.inept.blog.feature.rbac.model.dto.AddRolePermissionsDTO
import top.inept.blog.util.DTOGeneration
import kotlin.test.fail

class RoleBindPermissionTest : IntegrationTestBase() {

    @Test
    fun `角色绑定权限`() {
        val permissions = permissionRepository.findAll()

        val createRoleDTO = DTOGeneration.createRoleDTO()
        val dbRole = roleService.createRole(createRoleDTO)

        val adminArticlePermissions = permissions.filter { it.code.startsWith("admin:article") }

        //增量添加权限
        val newDbRoleVO = roleService.addRolePermissions(
            dbRole.id,
            AddRolePermissionsDTO(adminArticlePermissions.map { it.id }.toList())
        )

        if (newDbRoleVO.id != dbRole.id) {
            fail("与预期角色不符")
        }

        val actual = dbRole.permissionBindings.map { it.permission.id }.toSet()
        val expected = adminArticlePermissions.map { it.id }.toSet()

        assertTrue(actual.containsAll(expected))
    }
}