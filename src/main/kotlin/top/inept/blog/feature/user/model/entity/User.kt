package top.inept.blog.feature.user.model.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.user.model.entity.constraints.UserConstraints
import java.time.LocalDateTime

/**
 * 用户表
 *
 * @property id
 * @property nickname   昵称
 * @property username   用户名
 * @property email      邮箱
 * @property password   密码 BCrypt
 * @property role       角色
 * @property createdAt  创建时间
 * @property updatedAt  更新时间
 * @constructor Create empty User
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(name = UserConstraints.UNIQUE_NICKNAME, columnNames = ["nickname"]),
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

    @Column(name = "nickname", length = 16, nullable = false)
    var nickname: String,

    @Column(name = "username", length = 16, nullable = false)
    var username: String,

    @Column(name = "email", nullable = true)
    var email: String? = null,

    @Column(name = "password", nullable = false)
    var password: String,

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var roleBindings: MutableSet<UserRole> = mutableSetOf()
)