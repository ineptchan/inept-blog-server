package top.inept.blog.feature.admin.user.pojo.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * 开启JPA审计功能，需要在Application添加@EnableJpaAuditing
 */
@Entity
@Table(name = "users")
@EntityListeners(value = [AuditingEntityListener::class])   //注册审计监听器
 class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(length = 16, unique = true)
    var username: String,

    var password: String,

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
)