package top.inept.blog.feature.user.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "用户用户接口")
@RestController
@RequestMapping("/user/user")
@Validated
class UserUserController {

}