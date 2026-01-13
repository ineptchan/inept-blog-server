package top.inept.blog.feature.user.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Embeddable
data class RolePermissionId(
    @Column(name = "role_id")
    var roleId: Long = 0,

    @Column(name = "permission_id")
    var permissionId: Long = 0
) : java.io.Serializable

@Entity
@Table(name = "roles_permissions")
class RolePermission(
    @EmbeddedId
    var id: RolePermissionId = RolePermissionId(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    var role: Role,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id", nullable = false)
    var permission: Permission,

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)