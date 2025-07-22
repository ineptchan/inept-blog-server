package top.inept.blog.feature.admin.user.pojo.convert

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import top.inept.blog.feature.admin.user.pojo.dto.UserDto
import top.inept.blog.feature.admin.user.pojo.entity.User
import top.inept.blog.feature.admin.user.pojo.vo.UserVo

fun User.toUserVO() = UserVo(
    id = this.id,
    username = this.username,
    email = this.email,
)

fun UserDto.toUser(): User {
    return User(
        id = this.id,
        username = this.username,
        password = BCryptPasswordEncoder().encode(this.password),
        email = this.email,
        role = this.role,
    )
}