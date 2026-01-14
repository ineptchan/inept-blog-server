package top.inept.blog.feature.user.service

import org.springframework.data.domain.Page
import top.inept.blog.feature.user.model.dto.CreateUserDTO
import top.inept.blog.feature.user.model.dto.LoginUserDTO
import top.inept.blog.feature.user.model.dto.QueryUserDTO
import top.inept.blog.feature.user.model.dto.UpdateUserDTO
import top.inept.blog.feature.user.model.dto.UpdateUserProfileDTO
import top.inept.blog.feature.user.model.entity.User
import top.inept.blog.feature.user.model.vo.LoginUserVO

interface UserService {
    fun getUsers(queryUserDTO: QueryUserDTO): Page<User>
    fun getUserById(id: Long): User
    fun getUserByUsername(username: String): User
    fun createUser(createUserDTO: CreateUserDTO): User
    fun updateUser(updateUserDTO: UpdateUserDTO): User
    fun deleteUserById(id: Long)
    fun updateProfile(updateUserProfileDTO: UpdateUserProfileDTO): User
    fun getProfile(): User
}