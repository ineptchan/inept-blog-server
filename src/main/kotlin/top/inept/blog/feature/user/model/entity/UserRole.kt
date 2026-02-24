package top.inept.blog.feature.user.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.rbac.model.entity.Role
import java.time.Instant

@Embeddable
data class UserRoleId(
    @Column(name = "user_id")
    var userId: Long = 0,

    @Column(name = "role_id")
    var roleId: Long = 0
) : java.io.Serializable

@Entity
@Table(
    name = "users_roles",
    indexes = [
        Index(name = "idx_users_roles_user", columnList = "user_id"),
        Index(name = "idx_users_roles_role", columnList = "role_id")
    ]
)
@EntityListeners(value = [AuditingEntityListener::class])
class UserRole(
    @EmbeddedId
    var id: UserRoleId = UserRoleId(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    var role: Role,

    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),

    @LastModifiedDate
    var updatedAt: Instant? = null,
)