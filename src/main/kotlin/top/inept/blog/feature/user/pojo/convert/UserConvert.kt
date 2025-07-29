package top.inept.blog.feature.user.pojo.convert

import top.inept.blog.feature.user.pojo.dto.CreateUserDTO
import top.inept.blog.feature.user.pojo.entity.User
import top.inept.blog.feature.user.pojo.vo.UserPublicVO
import top.inept.blog.feature.user.pojo.vo.UserVO
import top.inept.blog.utils.PasswordUtil

fun User.toUserVO() = UserVO(
    id = this.id,
    nickname = this.nickname,
    username = this.username,
    email = this.email,
)

fun User.toUserPublicVO() = UserPublicVO(
    id = this.id,
    nickname = this.nickname,
)

fun CreateUserDTO.toUser() = User(
    nickname = this.nickname,
    username = this.username,
    password = PasswordUtil.encode(this.password),
    email = this.email,
    role = this.role,
)