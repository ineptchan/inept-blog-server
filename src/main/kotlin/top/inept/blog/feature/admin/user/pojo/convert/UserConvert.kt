package top.inept.blog.feature.admin.user.pojo.convert

import top.inept.blog.feature.admin.user.pojo.dto.UserDTO
import top.inept.blog.feature.admin.user.pojo.entity.User
import top.inept.blog.feature.admin.user.pojo.vo.UserVO
import top.inept.blog.utils.PasswordUtil

fun User.toUserVO() = UserVO(
    id = this.id,
    username = this.username,
)

fun UserDTO.toUser(): User {
    //TODO 密码加盐或者使用BCrypt

    return User(
        id = this.id,
        username = this.username,
        password = PasswordUtil.formatPassword(this.password),
    )
}