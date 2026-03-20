package top.inept.blog.feature.comment.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.comment.model.entity.constraints.CommentLikeConstraints
import top.inept.blog.feature.user.model.entity.User
import java.time.Instant

@Entity
@Table(
    name = "comment_like",
    uniqueConstraints = [
        UniqueConstraint(
            name = CommentLikeConstraints.UNIQUE_COMMENT_LIKE_USER_COMMENT,
            columnNames = ["comment_id", "user_id"]
        )
    ]
)
@EntityListeners(value = [AuditingEntityListener::class])
class CommentLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /**
     * 点赞的评论
     */
    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    var comment: Comment,

    /**
     * 点赞的用户
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),
)