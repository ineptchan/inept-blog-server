package top.inept.blog.feature.admin.user.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.constant.UserConstant
import top.inept.blog.feature.admin.user.pojo.convert.toUser
import top.inept.blog.feature.admin.user.pojo.convert.toUserVO
import top.inept.blog.feature.admin.user.pojo.dto.UserDTO
import top.inept.blog.feature.admin.user.pojo.dto.UserLoginDTO
import top.inept.blog.feature.admin.user.pojo.vo.UserLoginVO
import top.inept.blog.feature.admin.user.pojo.vo.UserVO
import top.inept.blog.feature.admin.user.service.UserService

@RestController
@RequestMapping("/admin/user")
class UserController(
    private val userService: UserService,
) {
    /**
     * 获取用户列表
     *
     * @return
     */
    @GetMapping
    fun getUsers(): Result<List<UserVO>> {
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
    fun getUserById(@PathVariable id: Long): Result<UserVO> {
        val user = userService.getUserById(id)
        if (user == null) {
            throw Exception(UserConstant.USER_NOT_FOUND)
        }
        return Result.Companion.success(user.toUserVO())
    }

    /**
     * 创建用户
     *
     * @param user
     * @return
     */
    @PostMapping
    fun createUser(@Validated @RequestBody user: UserDTO): UserVO {
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
    fun updateUser(@Validated @RequestBody user: UserDTO): UserVO {
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
    @GetMapping("/login")
    fun login(@Validated @RequestBody userLoginDTO: UserLoginDTO): Result<UserLoginVO> {
        val userLoginVO = userService.loginUser(userLoginDTO)
        return Result.success(userLoginVO)
    }
}