package top.inept.blog.feature.rbac.model.entity

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable

@Embeddable
data class RolePermissionId(
    @Column(name = "role_id")
    var roleId: Long = 0,

    @Column(name = "permission_id")
    var permissionId: Long = 0
) : Serializable

@Entity
@Table(name = "role_permission_table")
@EntityListeners(AuditingEntityListener::class)
class RolePermission(
    @EmbeddedId
    var id: RolePermissionId = RolePermissionId(),

    /**
     * 角色
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    var role: Role,

    /**
     * 权限
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id", nullable = false)
    var permission: Permission,
)