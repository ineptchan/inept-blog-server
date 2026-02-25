package top.inept.blog.feature.rbac.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.PageResponse
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.feature.rbac.model.convert.toRoleVO
import top.inept.blog.feature.rbac.model.dto.CreateRoleDTO
import top.inept.blog.feature.rbac.model.dto.QueryRoleDTO
import top.inept.blog.feature.rbac.model.dto.UpdateRoleDTO
import top.inept.blog.feature.rbac.model.vo.RoleVO
import top.inept.blog.feature.rbac.service.RoleService

@Tag(name = "角色接口")
@RestController
@RequestMapping("/role")
@Validated
class RoleController(
    private val roleService: RoleService
) {
    @PreAuthorize("hasAuthority('admin:read')")
    @Operation(summary = "获取角色列表")
    @GetMapping
    fun getRoles(@Valid dto: QueryRoleDTO): ResponseEntity<PageResponse<RoleVO>> {
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
    @PutMapping("/{id}")
    fun updateRole(@PathVariable id: Long, @Valid @RequestBody dto: UpdateRoleDTO): ResponseEntity<RoleVO> {
        return ResponseEntity.ok(roleService.updateRole(id, dto).toRoleVO())
    }

    @PreAuthorize("hasAuthority('admin:role:delete')")
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    fun deleteRole(@PathVariable id: Long) {
        roleService.deleteRole(id)
    }
}