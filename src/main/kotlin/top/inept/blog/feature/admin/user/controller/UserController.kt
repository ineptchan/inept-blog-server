package top.inept.blog.feature.admin.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.ApiResponse
import top.inept.blog.feature.admin.user.pojo.convert.toUser
import top.inept.blog.feature.admin.user.pojo.convert.toUserVO
import top.inept.blog.feature.admin.user.pojo.dto.CreateUserDTO
import top.inept.blog.feature.admin.user.pojo.dto.UserDTO
import top.inept.blog.feature.admin.user.pojo.vo.UserVO
import top.inept.blog.feature.admin.user.service.UserService

@Tag(name = "管理员用户接口")
@RestController("adminUserController")
@RequestMapping("/admin/user")
@Validated
class UserController(
    private val userService: UserService,
) {
    @Operation(summary = "获取用户列表")
    @GetMapping
    fun getUsers(): ApiResponse<List<UserVO>> {
        val users = userService.getUsers().map { it.toUserVO() }
        return ApiResponse.success(users)
    }

    @Operation(summary = "根据id获取用户")
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ApiResponse<UserVO> {
        return ApiResponse.success(userService.getUserById(id).toUserVO())
    }

    @Operation(summary = "创建用户")
    @PostMapping
    fun createUser(@Valid @RequestBody user: CreateUserDTO): ApiResponse<UserVO> {
        val user = userService.createUser(user.toUser())
        return ApiResponse.success(user.toUserVO())
    }

    @Operation(summary = "更新用户")
    @PutMapping
    fun updateUser(@Valid @RequestBody user: UserDTO): ApiResponse<UserVO> {
        val user = userService.updateUser(user.toUser())
        return ApiResponse.success(user.toUserVO())
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    fun deleteUserById(@PathVariable id: Long): ApiResponse<Boolean> {
        userService.deleteUserById(id)
        return ApiResponse.success(true)
    }
}