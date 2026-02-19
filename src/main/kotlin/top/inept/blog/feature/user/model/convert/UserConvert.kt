package top.inept.blog.feature.user.model.convert

import top.inept.blog.feature.user.model.entity.User
import top.inept.blog.feature.user.model.vo.UserInfoVO
import top.inept.blog.feature.user.model.vo.UserLiteVO
import top.inept.blog.feature.user.model.vo.UserVO

fun User.toUserVO() = UserVO(
    id = this.id,
    nickname = this.nickname,
    username = this.username,
    email = this.email,
)

fun User.toUserPublicVO() = UserLiteVO(
    id = this.id,
    nickname = this.nickname,
)

fun User.toUserInfoVO(permissionCodes: List<String>) = UserInfoVO(
    id = this.id,
    nickname = this.nickname,
    username = this.username,
    email = this.email,
    avatar = this.avatar ?: "",
    permissionCodes = permissionCodes
)