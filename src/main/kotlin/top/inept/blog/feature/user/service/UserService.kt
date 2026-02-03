package top.inept.blog.feature.user.service

import org.springframework.data.domain.Page
import top.inept.blog.feature.user.model.dto.CreateUserDTO
import top.inept.blog.feature.user.model.dto.QueryUserDTO
import top.inept.blog.feature.user.model.dto.UpdateUserDTO
import top.inept.blog.feature.user.model.dto.UpdateUserProfileDTO
import top.inept.blog.feature.user.model.entity.User
import top.inept.blog.feature.user.model.vo.UserInfoVO

interface UserService {
    fun getUsers(queryUserDTO: QueryUserDTO): Page<User>
    fun getUserById(id: Long): User
    fun getUserInfoById(id: Long): UserInfoVO
    fun getUserByUsername(username: String): User
    fun createUser(createUserDTO: CreateUserDTO): User
    fun updateUser(id: Long, dto: UpdateUserDTO): User
    fun deleteUserById(id: Long)
    fun updateProfile(dto: UpdateUserProfileDTO): User
    fun getProfile(): UserInfoVO
}