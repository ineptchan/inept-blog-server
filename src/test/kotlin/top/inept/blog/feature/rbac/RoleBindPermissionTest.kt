package top.inept.blog.feature.rbac

import top.inept.blog.IntegrationTestBase
import top.inept.blog.feature.rbac.model.dto.AddRolePermissionsDTO
import top.inept.blog.util.DTOGeneration
import kotlin.test.fail

class RoleBindPermissionTest : IntegrationTestBase() {
    fun `角色绑定权限`() {
        val permissions = permissionService.getPermissions()

        val createRoleDTO = DTOGeneration.createRoleDTO()
        val dbRole = roleService.createRole(createRoleDTO)

        val adminArticlePermissions = permissions.filter { it.code.startsWith("admin:article") }

        //增量添加权限
        roleService.addRolePermissions(dbRole.id, AddRolePermissionsDTO(adminArticlePermissions.map { it.id }.toList()))

        entityManager.refresh(dbRole)

        //判断是否全部绑定
        val size = (dbRole.permissionBindings.map { it.permission.id } - adminArticlePermissions.map { it.id }
            .toList()).size
        if (size == 0) {
            fail("权限数量与预期不符, 差${size}")
        }
    }
}