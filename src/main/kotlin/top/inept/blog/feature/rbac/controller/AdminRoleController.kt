package top.inept.blog.feature.rbac.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.PageResponse
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.feature.rbac.model.convert.toRoleVO
import top.inept.blog.feature.rbac.model.dto.*
import top.inept.blog.feature.rbac.model.vo.RolePermissionVO
import top.inept.blog.feature.rbac.model.vo.RoleVO
import top.inept.blog.feature.rbac.service.RoleService

@Tag(name = "角色接口")
@SecurityRequirement(name = "accessToken")
@RestController
@RequestMapping("/admin/role")
@Validated
class AdminRoleController(
    private val roleService: RoleService
) {
    @PreAuthorize("hasAuthority('admin:role:read')")
    @Operation(summary = "获取角色列表")
    @GetMapping
    fun getRoles(
        @Valid
        @ParameterObject
        @ModelAttribute
        dto: QueryRoleDTO
    ): ResponseEntity<PageResponse<RoleVO>> {
        return ResponseEntity.ok(roleService.getRoles(dto).toPageResponse { it.toRoleVO() })
    }

    @PreAuthorize("hasAuthority('admin:role:read')")
    @Operation(summary = "根据id获取角色")
    @GetMapping("/{id}")
    fun getRoleById(@PathVariable id: Long): ResponseEntity<RoleVO>? {
        return ResponseEntity.ok(roleService.getRoleById(id).toRoleVO())
    }

    @PreAuthorize("hasAuthority('admin:role:create')")
    @Operation(summary = "创建角色")
    @PostMapping
    fun createRole(@Valid @RequestBody dto: CreateRoleDTO): ResponseEntity<RoleVO> {
        return ResponseEntity.ok(roleService.createRole(dto).toRoleVO())
    }

    @PreAuthorize("hasAuthority('admin:role:update')")
    @Operation(summary = "更新角色")
    @PatchMapping("/{id}")
    fun updateRole(@PathVariable id: Long, @Valid @RequestBody dto: UpdateRoleDTO): ResponseEntity<RoleVO> {
        return ResponseEntity.ok(roleService.updateRole(id, dto).toRoleVO())
    }

    @PreAuthorize("hasAuthority('admin:role:delete')")
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    fun deleteRole(@PathVariable id: Long) {
        roleService.deleteRole(id)
    }

    @PreAuthorize("hasAuthority('admin:role:permission:read')")
    @Operation(summary = "获取角色绑定的权限")
    @GetMapping("/{id}/permissions")
    fun getRoleBindPermissions(@PathVariable id: Long): ResponseEntity<RolePermissionVO> {
        return ResponseEntity.ok(roleService.getRoleBindPermissions(id))
    }

    @PreAuthorize("hasAuthority('admin:role:permission:update')")
    @Operation(summary = "全量替换角色绑定的权限")
    @PutMapping("/{id}/permissions")
    fun replaceRolePermissions(
        @PathVariable id: Long,
        @Valid @RequestBody dto: ReplaceRolePermissionsDTO
    ): ResponseEntity<RolePermissionVO> {
        return ResponseEntity.ok(roleService.replaceRolePermissions(id, dto))
    }

    @PreAuthorize("hasAuthority('admin:role:permission:update')")
    @Operation(summary = "增量添加角色绑定的权限")
    @PostMapping("/{id}/permissions")
    fun addRolePermissions(
        @PathVariable id: Long,
        @Valid @RequestBody dto: AddRolePermissionsDTO
    ): ResponseEntity<RolePermissionVO> {
        return ResponseEntity.ok(roleService.addRolePermissions(id, dto))
    }

    @PreAuthorize("hasAuthority('admin:role:permission:delete')")
    @Operation(summary = "移除单个角色绑定的权限")
    @PostMapping("/{roleId}/permissions/{permId}")
    fun removeRolePermission(
        @PathVariable roleId: Long,
        @PathVariable permId: Long
    ): ResponseEntity<RolePermissionVO> {
        return ResponseEntity.ok(roleService.removeRolePermission(roleId, permId))
    }
}