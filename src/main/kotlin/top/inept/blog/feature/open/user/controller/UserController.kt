package top.inept.blog.feature.open.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.base.ApiResponse
import top.inept.blog.feature.admin.user.pojo.dto.LoginUserDto
import top.inept.blog.feature.admin.user.pojo.vo.LoginUserVo
import top.inept.blog.feature.admin.user.service.UserService

@Tag(name = "公开用户接口")
@RestController( "openUserController")
@RequestMapping("/open")
@Validated
class UserController(
    private val userService: UserService,
) {
    @Operation(summary = "登录")
    @PostMapping("/login")
    fun login(@RequestBody userLoginDTO: LoginUserDto): ApiResponse<LoginUserVo> {
        val userLoginVO = userService.loginUser(userLoginDTO)
        return ApiResponse.Companion.success(userLoginVO)
    }
}