package top.inept.blog.feature.rbac.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.rbac.model.entity.constraints.RoleConstraints
import top.inept.blog.feature.user.model.entity.UserRole
import java.time.Instant

@Entity
@Table(
    name = "roles",
    uniqueConstraints = [
        UniqueConstraint(name = RoleConstraints.UNIQUE_CODE, columnNames = ["code"]),
    ]
)
@EntityListeners(value = [AuditingEntityListener::class])
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    /**
     * code
     */
    @Column(name = "code", nullable = false, length = 64)
    var code: String,

    /**
     * 名字
     */
    @Column(name = "name", nullable = false, length = 32)
    var name: String,

    /**
     * 描述
     */
    @Column(name = "description")
    var description: String? = null,

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: Instant,

    /**
     * 更新时间
     */
    @LastModifiedDate
    var updatedAt: Instant? = null,

    /**
     * 绑定的用户
     */
    @OneToMany(mappedBy = "role")
    var userBindings: MutableSet<UserRole> = mutableSetOf(),

    /**
     * 绑定的权限
     */
    @OneToMany(mappedBy = "role", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("id ASC")
    var permissionBindings: MutableSet<RolePermission> = mutableSetOf()
) {
    fun addPermission(permission: Permission) {
        val binding = RolePermission(
            id = RolePermissionId(this.id, permission.id),
            role = this,
            permission = permission
        )
        permissionBindings.add(binding)
    }

    fun removePermissionById(permissionId: Long) {
        permissionBindings.removeIf { it.permission.id == permissionId }
    }
}