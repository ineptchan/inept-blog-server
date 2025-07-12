package top.inept.blog.feature.admin.user.pojo.convert

import top.inept.blog.feature.admin.user.pojo.dto.UserDto
import top.inept.blog.feature.admin.user.pojo.entity.User
import top.inept.blog.feature.admin.user.pojo.vo.UserVo
import top.inept.blog.utils.PasswordUtil

fun User.toUserVO() = UserVo(
    id = this.id,
    username = this.username,
)

fun UserDto.toUser(): User {
    //TODO 密码加盐或者使用BCrypt

    return User(
        id = this.id,
        username = this.username,
        password = PasswordUtil.formatPassword(this.password),
    )
}