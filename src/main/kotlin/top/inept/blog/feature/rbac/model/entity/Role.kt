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
    //增量更新
    fun addPermissions(permissions: Collection<Permission>) {
        val distinctPermissions = permissions.distinctBy { it.id }

        // 获取已经绑定的权限 ID
        val existingPermissionIds = this.permissionBindings.map { it.permission.id }.toSet()

        val newBindings = distinctPermissions
            .filter { it.id !in existingPermissionIds }
            .map { permission ->
                RolePermission(
                    id = RolePermissionId(this.id, permission.id),
                    role = this,
                    permission = permission
                )
            }

        this.permissionBindings.addAll(newBindings)
    }

    //全量更新
    fun replacePermissions(newPermissions: Collection<Permission>) {
        val newPermissionIds = newPermissions.map { it.id }.toSet()

        // 移除不在新列表中的权限
        permissionBindings.removeIf { it.permission.id !in newPermissionIds }

        // 添加原本不存在的权限
        addPermissions(newPermissions)
    }
}