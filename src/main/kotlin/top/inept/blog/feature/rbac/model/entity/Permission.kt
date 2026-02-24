package top.inept.blog.feature.rbac.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.rbac.model.entity.constraints.PermissionConstraints
import java.time.Instant

/**
 * Permission
 *
 * @property id
 * @property code
 * @property name
 * @property description
 * @property createdAt
 * @property updatedAt
 * @property roleBindings
 * @constructor Create empty Permission
 */
@Entity
@Table(
    name = "permissions",
    uniqueConstraints = [
        UniqueConstraint(name = PermissionConstraints.UNIQUE_CODE, columnNames = ["code"]),
        UniqueConstraint(name = PermissionConstraints.UNIQUE_NAME, columnNames = ["name"])
    ]
)
@EntityListeners(value = [AuditingEntityListener::class])
class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @Column(name = "code", nullable = false)
    var code: String,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "description")
    var description: String? = null,

    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),

    @LastModifiedDate
    var updatedAt: Instant? = null,

    @OneToMany(mappedBy = "permission")
    var roleBindings: MutableSet<RolePermission> = mutableSetOf()
)