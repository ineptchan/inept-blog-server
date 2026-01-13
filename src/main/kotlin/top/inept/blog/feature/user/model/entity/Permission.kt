package top.inept.blog.feature.user.model.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.user.model.entity.constraints.PermissionConstraints
import java.time.LocalDateTime

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

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "permission")
    var roleBindings: MutableSet<RolePermission> = mutableSetOf()
)