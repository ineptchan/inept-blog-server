package top.inept.blog.feature.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.ApiResponse
import top.inept.blog.feature.user.model.convert.toUserVO
import top.inept.blog.feature.user.model.dto.UpdateUserProfileDTO
import top.inept.blog.feature.user.model.vo.UserVO
import top.inept.blog.feature.user.service.UserService

@Tag(name = "用户用户接口")
@RestController
@RequestMapping("/user/user")
@Validated
class UserUserController(
    private val userService: UserService,
) {
    @Operation(summary = "获得用户资料")
    @GetMapping
    fun getProfile(): ApiResponse<UserVO> {
        return ApiResponse.success(userService.getProfile().toUserVO())
    }

    @Operation(summary = "更新用户资料")
    @PutMapping
    fun updateProfile(@Valid @RequestBody updateUserProfileDTO: UpdateUserProfileDTO): ApiResponse<UserVO> {
        return ApiResponse.success(userService.updateProfile(updateUserProfileDTO).toUserVO())
    }
}