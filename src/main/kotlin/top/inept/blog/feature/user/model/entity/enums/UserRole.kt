package top.inept.blog.feature.user.model.entity.enums

import org.springframework.security.core.GrantedAuthority

enum class UserRole : GrantedAuthority {
    USER,
    ADMIN;

    override fun getAuthority(): String {
        return "ROLE_$name"
    }
}