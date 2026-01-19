package top.inept.blog.feature.auth.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.user.model.entity.User
import top.inept.blog.feature.auth.model.entity.constraints.RefreshTokenConstraints
import java.time.Instant
import java.time.LocalDateTime

/**
 * Refresh token
 *
 * @property id
 * @property user       用户
 * @property tokenHash  token的sha256值
 * @property expiresAt  过期时间
 * @property createdAt  创建时间
 * @property lastUsedAt 最后使用时间
 * @property revokedAt  撤销时间
 * @property userAgent  浏览器Agent
 * @property ip         ip
 * @property replacedBy 旧token，防止窃取token
 * @constructor Create empty Refresh token
 */
@Entity
@Table(
    name = "refresh_token",
    uniqueConstraints = [
        UniqueConstraint(name = RefreshTokenConstraints.UNIQUE_TOKEN_HASH, columnNames = ["token_hash"]),
        UniqueConstraint(columnNames = ["replaced_by_id"])
    ]
)
@EntityListeners(AuditingEntityListener::class)
class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "token_hash", nullable = false, length = 64)
    var tokenHash: String,

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant,

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "last_used_at")
    var lastUsedAt: Instant? = null,

    @Column(name = "revoked_at")
    var revokedAt: Instant? = null,

    @Column(name = "user_agent")
    var userAgent: String? = null,

    @Column(name = "ip", length = 45)
    var ip: String? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replaced_by_id")
    var replacedBy: RefreshToken? = null
)