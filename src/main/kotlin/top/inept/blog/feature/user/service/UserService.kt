package top.inept.blog.feature.user.service

import top.inept.blog.base.PageResponse
import top.inept.blog.feature.user.model.dto.*
import top.inept.blog.feature.user.model.entity.User
import top.inept.blog.feature.user.model.vo.UserDetailVO
import top.inept.blog.feature.user.model.vo.UserRolesVO

interface UserService {
    fun getUsers(dto: QueryUserDTO): PageResponse<UserRolesVO>
    fun getUserById(id: Long): User
    fun getUserDetailById(id: Long): UserDetailVO
    fun getUserByUsername(username: String): User
    fun createUser(dto: CreateUserDTO): User
    fun updateUser(id: Long, dto: UpdateUserDTO): User
    fun deleteUserById(id: Long)
    fun updateProfile(dto: UpdateUserProfileDTO): User
    fun getProfile(): UserDetailVO
    fun replaceUserRoles(id: Long, dto: ReplaceUserRolesDTO): UserRolesVO
    fun addUserRoles(id: Long, dto: AddUserRolesDTO): UserRolesVO
    fun removeUserRole(userId: Long, roleId: Long): UserRolesVO
}