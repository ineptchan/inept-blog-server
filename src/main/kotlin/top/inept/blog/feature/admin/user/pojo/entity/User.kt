package top.inept.blog.feature.admin.user.pojo.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.admin.user.pojo.entity.enums.UserRole
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
@Table(name = "users")
@EntityListeners(value = [AuditingEntityListener::class])   //注册审计监听器
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(length = 16, unique = true, nullable = false)
    var nickname: String,

    @Column(length = 16, unique = true, nullable = false)
    var username: String,

    @Column(unique = true, nullable = true)
    var email: String? = null,

    @Column(nullable = false)
    var password: String,

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    var role: UserRole = UserRole.USER,

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
)