package top.inept.blog.feature.admin.user.service

import top.inept.blog.feature.admin.user.pojo.dto.CreateUserDTO
import top.inept.blog.feature.admin.user.pojo.dto.LoginUserDTO
import top.inept.blog.feature.admin.user.pojo.dto.UpdateUserDTO
import top.inept.blog.feature.admin.user.pojo.entity.User
import top.inept.blog.feature.admin.user.pojo.vo.LoginUserVO

interface UserService {
    fun getUsers(): List<User>
    fun getUserById(id: Long): User
    fun getUserByUsername(username: String): User
    fun createUser(createUserDTO: CreateUserDTO): User
    fun updateUser(updateUserDTO: UpdateUserDTO): User
    fun deleteUserById(id: Long)
    fun loginUser(userLoginDTO: LoginUserDTO): LoginUserVO
}