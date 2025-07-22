package top.inept.blog.feature.admin.user.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.ApiResponse
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
    fun getUsers(): ApiResponse<List<UserVo>> {
        val users = userService.getUsers().map { it.toUserVO() }
        return ApiResponse.success(users)
    }

    /**
     * 按id获取用户
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ApiResponse<UserVo> {
        return ApiResponse.success(userService.getUserById(id).toUserVO())
    }

    /**
     * 创建用户
     *
     * @param user
     * @return
     */
    @PostMapping
    fun createUser(@RequestBody user: UserDto): ApiResponse<UserVo> {
        val user = userService.createUser(user.toUser())
        return ApiResponse.success(user.toUserVO())
    }

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    @PutMapping
    fun updateUser(@RequestBody user: UserDto): ApiResponse<UserVo> {
        val user = userService.updateUser(user.toUser())
        return ApiResponse.success(user.toUserVO())
    }

    /**
     * 删除用户
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    fun deleteUserById(@PathVariable id: Long): ApiResponse<Boolean> {
        //TODO 成功提示
        userService.deleteUserById(id)
        return ApiResponse.success(true)
    }

    /**
     * 登录
     *
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    fun login(@RequestBody userLoginDTO: LoginUserDto): ApiResponse<LoginUserVo> {
        val userLoginVO = userService.loginUser(userLoginDTO)
        return ApiResponse.success(userLoginVO)
    }
}