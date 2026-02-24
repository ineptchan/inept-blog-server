package top.inept.blog.feature.user.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.rbac.model.entity.Role
import top.inept.blog.feature.user.model.entity.constraints.UserConstraints
import java.time.Instant

/**
 * 用户表
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(name = UserConstraints.UNIQUE_USERNAME, columnNames = ["username"]),
        UniqueConstraint(name = UserConstraints.UNIQUE_EMAIL, columnNames = ["email"]),
    ]
)
@EntityListeners(value = [AuditingEntityListener::class])
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    /**
     * 昵称
     */
    @Column(name = "nickname", length = 16, nullable = false)
    var nickname: String,

    /**
     * 头像
     */
    @Column(name = "avatar")
    var avatar: String? = null,

    /**
     * 用户名
     */
    @Column(name = "username", length = 16, nullable = false)
    var username: String,

    /**
     * 邮箱
     */
    @Column(name = "email", nullable = true)
    var email: String? = null,

    /**
     * 密码   BCrypt
     */
    @Column(name = "password", nullable = false)
    var password: String,

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),

    /**
     * 更新时间
     */
    @LastModifiedDate
    var updatedAt: Instant? = null,

    /**
     * 角色绑定
     */
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var roleBindings: MutableSet<UserRole> = mutableSetOf()
) {
    fun bindRoles(roles: Collection<Role>) {
        //获取已经绑定的角色
        val existingRoleIds = this.roleBindings.map { it.role.id }.toSet()

        val newBindings = roles
            .filter { it.id !in existingRoleIds }
            .map { role ->
                UserRole(
                    id = UserRoleId(this.id, role.id),
                    user = this,
                    role = role
                )
            }
        this.roleBindings.addAll(newBindings)
    }

    fun updateRoles(newRoles: Collection<Role>) {
        val newRoleIds = newRoles.map { it.id }.toSet()

        //移除不在新列表中的
        roleBindings.removeIf { it.role.id !in newRoleIds }

        //添加原本不存在的
        bindRoles(newRoles)
    }
}