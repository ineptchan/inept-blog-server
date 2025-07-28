package top.inept.blog.feature.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.base.ApiResponse
import top.inept.blog.feature.user.pojo.dto.LoginUserDTO
import top.inept.blog.feature.user.pojo.vo.LoginUserVO
import top.inept.blog.feature.user.service.UserService

@Tag(name = "公开用户接口")
@RestController
@RequestMapping("/open/user")
@Validated
class OpenUserController(
    private val userService: UserService,
) {
    @Operation(summary = "登录")
    @PostMapping("/login")
    fun login(@Valid @RequestBody userLoginDTO: LoginUserDTO): ApiResponse<LoginUserVO> {
        val userLoginVO = userService.loginUser(userLoginDTO)
        return ApiResponse.Companion.success(userLoginVO)
    }
}