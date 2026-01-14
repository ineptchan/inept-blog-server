package top.inept.blog.feature.user.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.feature.user.service.UserService

@Tag(name = "公开用户接口")
@RestController
@RequestMapping("/public/user")
@Validated
class OpenUserController(
    private val userService: UserService,
) {
    //TODO 考虑删除
}