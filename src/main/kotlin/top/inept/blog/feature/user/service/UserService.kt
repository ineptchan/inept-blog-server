package top.inept.blog.feature.user.service

import top.inept.blog.feature.user.pojo.dto.CreateUserDTO
import top.inept.blog.feature.user.pojo.dto.LoginUserDTO
import top.inept.blog.feature.user.pojo.dto.UpdateUserDTO
import top.inept.blog.feature.user.pojo.dto.UpdateUserProfileDTO
import top.inept.blog.feature.user.pojo.entity.User
import top.inept.blog.feature.user.pojo.vo.LoginUserVO

interface UserService {
    fun getUsers(): List<User>
    fun getUserById(id: Long): User
    fun getUserByUsername(username: String): User
    fun createUser(createUserDTO: CreateUserDTO): User
    fun updateUser(updateUserDTO: UpdateUserDTO): User
    fun deleteUserById(id: Long)
    fun loginUser(userLoginDTO: LoginUserDTO): LoginUserVO
    fun updateProfile(updateUserProfileDTO: UpdateUserProfileDTO): User
    fun getProfile(): User
}