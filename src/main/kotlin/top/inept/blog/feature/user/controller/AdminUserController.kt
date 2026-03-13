package top.inept.blog.feature.user.controller

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
import top.inept.blog.feature.user.model.convert.toUserVO
import top.inept.blog.feature.user.model.dto.*
import top.inept.blog.feature.user.model.vo.UserDetailVO
import top.inept.blog.feature.user.model.vo.UserRolesVO
import top.inept.blog.feature.user.model.vo.UserVO
import top.inept.blog.feature.user.service.UserService

@Tag(name = "用户接口")
@SecurityRequirement(name = "accessToken")
@RestController
@RequestMapping("/admin/user")
@Validated
class AdminUserController(
    private val userService: UserService,
) {
    @PreAuthorize("hasAuthority('admin:user:read')")
    @Operation(summary = "获取用户列表")
    @GetMapping
    fun getUsers(
        @Valid
        @ParameterObject
        @ModelAttribute
        dto: QueryUserDTO
    ): ResponseEntity<PageResponse<UserRolesVO>> {
        return ResponseEntity.ok(userService.getUsers(dto))
    }

    @PreAuthorize("hasAuthority('admin:user:read')")
    @Operation(summary = "根据id获取用户")
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserDetailVO> {
        return ResponseEntity.ok(userService.getUserDetailById(id))
    }

    @PreAuthorize("hasAuthority('admin:user:create')")
    @Operation(summary = "创建用户")
    @PostMapping
    fun createUser(@Valid @RequestBody dto: CreateUserDTO): ResponseEntity<UserVO> {
        return ResponseEntity.ok(userService.createUser(dto).toUserVO())
    }

    @PreAuthorize("hasAuthority('admin:user:update')")
    @Operation(summary = "更新用户")
    @PatchMapping("/{id}")
    fun updateUser(@Valid @RequestBody dto: UpdateUserDTO, @PathVariable id: Long): ResponseEntity<UserVO> {
        return ResponseEntity.ok(userService.updateUser(id, dto).toUserVO())
    }

    @PreAuthorize("hasAuthority('admin:user:delete')")
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    fun deleteUserById(@PathVariable id: Long): ResponseEntity<Boolean> {
        userService.deleteUserById(id)
        return ResponseEntity.ok(true)
    }

    @PreAuthorize("hasAuthority('admin:user:role:update')")
    @Operation(summary = "全量替换用户绑定的角色")
    @PutMapping("/{id}/roles")
    fun replaceUserRoles(
        @PathVariable id: Long,
        @Valid @RequestBody dto: ReplaceUserRolesDTO
    ): ResponseEntity<UserRolesVO> {
        return ResponseEntity.ok(userService.replaceUserRoles(id, dto))
    }

    @PreAuthorize("hasAuthority('admin:user:role:update')")
    @Operation(summary = "增量替换用户绑定的角色")
    @PostMapping("/{id}/roles")
    fun addUserRoles(
        @PathVariable id: Long,
        @Valid @RequestBody dto: AddUserRolesDTO
    ): ResponseEntity<UserRolesVO> {
        return ResponseEntity.ok(userService.addUserRoles(id, dto))
    }

    @PreAuthorize("hasAuthority('admin:user:role:delete')")
    @Operation(summary = "移除单个用户绑定的角色")
    @DeleteMapping("/{userId}/roles/{roleId}")
    fun removeUserRole(
        @PathVariable userId: Long,
        @PathVariable roleId: Long
    ): ResponseEntity<UserRolesVO> {
        return ResponseEntity.ok(userService.removeUserRole(userId, roleId))
    }
}