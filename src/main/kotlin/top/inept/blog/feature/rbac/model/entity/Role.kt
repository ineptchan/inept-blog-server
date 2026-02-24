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

    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: Instant,

    @LastModifiedDate
    var updatedAt: Instant? = null,

    @OneToMany(mappedBy = "role")
    var userBindings: MutableSet<UserRole> = mutableSetOf(),

    @OneToMany(mappedBy = "role", cascade = [CascadeType.ALL], orphanRemoval = true)
    var permissionBindings: MutableSet<RolePermission> = mutableSetOf()
)