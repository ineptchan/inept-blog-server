package top.inept.blog.feature.admin.user.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.feature.admin.user.pojo.convert.toUser
import top.inept.blog.feature.admin.user.pojo.convert.toUserVO
import top.inept.blog.feature.admin.user.pojo.dto.LoginUserDto
import top.inept.blog.feature.admin.user.pojo.dto.UserDto
import top.inept.blog.feature.admin.user.pojo.vo.LoginUserVo
import top.inept.blog.feature.admin.user.pojo.vo.UserVo
import top.inept.blog.feature.admin.user.service.UserService

@RestController
@RequestMapping("/admin/user")
@Validated
class UserController(
    private val userService: UserService,
) {
    /**
     * 获取用户列表
     *
     * @return
     */
    @GetMapping
    fun getUsers(): Result<List<UserVo>> {
        val users = userService.getUsers().map { it.toUserVO() }
        return Result.Companion.success(users)
    }

    /**
     * 按id获取用户
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): Result<UserVo> {
        return Result.Companion.success(userService.getUserById(id).toUserVO())
    }

    /**
     * 创建用户
     *
     * @param user
     * @return
     */
    @PostMapping
    fun createUser(@RequestBody user: UserDto): UserVo {
        val user = userService.createUser(user.toUser())
        return user.toUserVO()
    }

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    @PutMapping
    fun updateUser(@RequestBody user: UserDto): UserVo {
        val user = userService.updateUser(user.toUser())
        return user.toUserVO()
    }

    /**
     * 删除用户
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    fun deleteUserById(@PathVariable id: Long) {
        userService.deleteUserById(id)
    }

    /**
     * 登录
     *
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    fun login(@RequestBody userLoginDTO: LoginUserDto): Result<LoginUserVo> {
        val userLoginVO = userService.loginUser(userLoginDTO)
        return Result.success(userLoginVO)
    }
}