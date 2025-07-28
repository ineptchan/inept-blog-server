package top.inept.blog.feature.admin.user.pojo.convert

import top.inept.blog.feature.admin.user.pojo.dto.CreateUserDTO
import top.inept.blog.feature.admin.user.pojo.dto.UpdateUserDTO
import top.inept.blog.feature.admin.user.pojo.entity.User
import top.inept.blog.feature.admin.user.pojo.vo.UserPublicVO
import top.inept.blog.feature.admin.user.pojo.vo.UserVO
import top.inept.blog.utils.PasswordUtil

fun User.toUserVO() = UserVO(
    id = this.id,
    username = this.username,
    email = this.email,
)

fun User.toUserPublicVO() = UserPublicVO(
    id = this.id,
    username = this.username,
)

fun UpdateUserDTO.toUser(): User {
    return User(
        id = this.id,
        username = this.username,
        password = PasswordUtil.encode(this.password),
        email = this.email,
        role = this.role,
    )
}

fun CreateUserDTO.toUser(): User {
    return User(
        username = this.username,
        password = PasswordUtil.encode(this.password),
        email = this.email,
        role = this.role,
    )
}