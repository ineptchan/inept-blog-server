package top.inept.blog.feature.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.feature.user.model.convert.toUserVO
import top.inept.blog.feature.user.model.dto.UpdateUserProfileDTO
import top.inept.blog.feature.user.model.vo.UserInfoVO
import top.inept.blog.feature.user.model.vo.UserVO
import top.inept.blog.feature.user.service.UserService

@Tag(name = "用户接口")
@SecurityRequirement(name = "accessToken")
@RestController
@RequestMapping("/user/user")
@Validated
class UserUserController(
    private val userService: UserService,
) {
    @PreAuthorize("hasAuthority('user:user:read')")
    @Operation(summary = "获得用户资料")
    @GetMapping
    fun getProfile(): ResponseEntity<UserInfoVO> {
        return ResponseEntity.ok(userService.getProfile())
    }

    @PreAuthorize("hasAuthority('user:user:modify')")
    @Operation(summary = "更新用户资料")
    @PatchMapping
    fun updateProfile(@Valid @ModelAttribute updateUserProfileDTO: UpdateUserProfileDTO): ResponseEntity<UserVO> {
        return ResponseEntity.ok(userService.updateProfile(updateUserProfileDTO).toUserVO())
    }
}