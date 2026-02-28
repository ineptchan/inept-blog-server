package top.inept.blog.feature.rbac.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.PageResponse
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.feature.rbac.model.convert.toPermissionVO
import top.inept.blog.feature.rbac.model.dto.QueryPermissionDTO
import top.inept.blog.feature.rbac.model.dto.UpdatePermissionDTO
import top.inept.blog.feature.rbac.model.vo.PermissionVO
import top.inept.blog.feature.rbac.service.PermissionService

@Tag(name = "权限接口")
@SecurityRequirement(name = "accessToken")
@RestController
@RequestMapping("/permission")
@Validated
class AdminPermissionController(
    private val permissionService: PermissionService
) {
    @PreAuthorize("hasAuthority('admin:permission:read')")
    @Operation(summary = "获取权限列表")
    @GetMapping
    fun getPermissions(@Valid dto: QueryPermissionDTO): ResponseEntity<PageResponse<PermissionVO>> {
        return ResponseEntity.ok(permissionService.getPermissions(dto).toPageResponse { it.toPermissionVO() })
    }

    @PreAuthorize("hasAuthority('admin:permission:read')")
    @Operation(summary = "根据id获取权限")
    @GetMapping("/{id}")
    fun getPermissionById(@PathVariable id: Long): ResponseEntity<PermissionVO> {
        return ResponseEntity.ok(permissionService.getPermissionById(id).toPermissionVO())
    }

    @PreAuthorize("hasAuthority('admin:permission:update')")
    @Operation(summary = "更新权限")
    @PutMapping("/{id}")
    fun updatePermission(
        @PathVariable id: Long,
        @Valid @RequestBody dto: UpdatePermissionDTO
    ): ResponseEntity<PermissionVO> {
        return ResponseEntity.ok(permissionService.updatePermission(id, dto).toPermissionVO())
    }
}