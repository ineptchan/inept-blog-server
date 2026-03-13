package top.inept.blog.feature.user.model.convert

import top.inept.blog.feature.rbac.model.convert.toRoleVO
import top.inept.blog.feature.user.model.entity.User
import top.inept.blog.feature.user.model.vo.UserDetailVO
import top.inept.blog.feature.user.model.vo.UserLiteVO
import top.inept.blog.feature.user.model.vo.UserRolesVO
import top.inept.blog.feature.user.model.vo.UserVO

fun User.toUserVO() = UserVO(
    id = this.id,
    nickname = this.nickname,
    username = this.username,
    email = this.email,
    status = this.status
)

fun User.toUserPublicVO() = UserLiteVO(
    id = this.id,
    nickname = this.nickname,
)

fun User.toUserDetailVO(permissionCodes: List<String>) = UserDetailVO(
    id = this.id,
    nickname = this.nickname,
    username = this.username,
    email = this.email,
    avatar = this.avatar ?: "",
    status = this.status,
    permissionCodes = permissionCodes,
    roles = this.roleBindings.map { it.role.toRoleVO() }
)

/**
 * User转UserRolesVO
 *
 * 小心roles导致 sql n+1问题
 */
fun User.toUserRolesVO() = UserRolesVO(
    id = this.id,
    nickname = this.nickname,
    username = this.username,
    email = this.email,
    status = this.status,
    roles = this.roleBindings.map { it.role.toRoleVO() }
)