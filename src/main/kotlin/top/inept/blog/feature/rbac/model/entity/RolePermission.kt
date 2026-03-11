package top.inept.blog.feature.rbac.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.Instant

@Embeddable
data class RolePermissionId(
    @Column(name = "role_id")
    var roleId: Long = 0,

    @Column(name = "permission_id")
    var permissionId: Long = 0
) : Serializable

@Entity
@Table(name = "roles_permissions")
@EntityListeners(AuditingEntityListener::class)
class RolePermission(
    @EmbeddedId
    var id: RolePermissionId = RolePermissionId(),

    /**
     * 角色
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    var role: Role,

    /**
     * 权限
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id", nullable = false)
    var permission: Permission,

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
)