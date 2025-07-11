package top.inept.blog.feature.admin.user.service

import top.inept.blog.feature.admin.user.pojo.dto.UserLoginDTO
import top.inept.blog.feature.admin.user.pojo.entity.User
import top.inept.blog.feature.admin.user.pojo.vo.UserLoginVO

interface UserService {
    fun getUsers(): List<User>
    fun getUserById(id: Long): User?
    fun createUser(user: User): User
    fun updateUser(user: User): User
    fun deleteUserById(id: Long)
    fun loginUser(userLoginDTO: UserLoginDTO): UserLoginVO
}