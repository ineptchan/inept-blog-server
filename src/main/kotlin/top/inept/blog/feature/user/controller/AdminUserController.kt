package top.inept.blog.feature.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.ApiResponse
import top.inept.blog.base.PageResponse
import top.inept.blog.extensions.toApiResponse
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.feature.user.model.convert.toUserVO
import top.inept.blog.feature.user.model.dto.CreateUserDTO
import top.inept.blog.feature.user.model.dto.QueryUserDTO
import top.inept.blog.feature.user.model.dto.UpdateUserDTO
import top.inept.blog.feature.user.model.vo.UserVO
import top.inept.blog.feature.user.service.UserService

@Tag(name = "管理员用户接口")
@RestController
@RequestMapping("/admin/user")
@Validated
class AdminUserController(
    private val userService: UserService,
) {
    @Operation(summary = "获取用户列表")
    @GetMapping
    fun getUsers(@Valid queryUserDTO: QueryUserDTO): ApiResponse<PageResponse<UserVO>> {
        return userService
            .getUsers(queryUserDTO)
            .toPageResponse { it.toUserVO() }
            .toApiResponse()
    }

    @Operation(summary = "根据id获取用户")
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ApiResponse<UserVO> {
        return ApiResponse.success(userService.getUserById(id).toUserVO())
    }

    @Operation(summary = "创建用户")
    @PostMapping
    fun createUser(@Valid @RequestBody createUserDTO: CreateUserDTO): ApiResponse<UserVO> {
        return ApiResponse.success(userService.createUser(createUserDTO).toUserVO())
    }

    @Operation(summary = "更新用户")
    @PutMapping
    fun updateUser(@Valid @RequestBody updateUserDTO: UpdateUserDTO): ApiResponse<UserVO> {
        return ApiResponse.success(userService.updateUser(updateUserDTO).toUserVO())
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    fun deleteUserById(@PathVariable id: Long): ApiResponse<Boolean> {
        userService.deleteUserById(id)
        return ApiResponse.success(true)
    }
}