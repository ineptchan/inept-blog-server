package top.inept.blog.feature.admin.user.pojo.convert

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import top.inept.blog.feature.admin.user.pojo.dto.CreateUserDTO
import top.inept.blog.feature.admin.user.pojo.dto.UpdateUserDTO
import top.inept.blog.feature.admin.user.pojo.entity.User
import top.inept.blog.feature.admin.user.pojo.vo.UserVO

fun User.toUserVO() = UserVO(
    id = this.id,
    username = this.username,
    email = this.email,
)

fun UpdateUserDTO.toUser(): User {
    return User(
        id = this.id,
        username = this.username,
        password = BCryptPasswordEncoder().encode(this.password),
        email = this.email,
        role = this.role,
    )
}

fun CreateUserDTO.toUser(): User {
    return User(
        username = this.username,
        password = BCryptPasswordEncoder().encode(this.password),
        email = this.email,
        role = this.role,
    )
}