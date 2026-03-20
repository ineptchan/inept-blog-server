package top.inept.blog.feature.comment.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.article.model.entity.Article
import top.inept.blog.feature.comment.model.entity.enums.CommentStatus
import top.inept.blog.feature.user.model.entity.User
import java.time.Instant

@Entity
@Table(name = "comments")
@EntityListeners(value = [AuditingEntityListener::class])
class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /**
     * 评论的内容
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    /**
     * 评论的状态
     * 待审核、已发布、已删除、垃圾评论
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: CommentStatus = CommentStatus.PENDING,

    /**
     * 点赞数
     */
    @Column(nullable = false)
    var likeCount: Int = 0,

    /**
     * 评论的文章
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    var article: Article,

    /**
     * 评论的用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    /**
     * 父级评论
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    var parentComment: Comment? = null,

    /**
     * 子评论
     */
    @OneToMany(
        mappedBy = "parentComment",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    var replies: MutableList<Comment> = mutableListOf(),

    /**
     * 点赞
     */
    @OneToMany(
        mappedBy = "comment",
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    var commentLikes: MutableSet<CommentLike> = mutableSetOf(),

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),

    /**
     * 更新时间
     */
    @LastModifiedDate
    var updatedAt: Instant? = null,

    /**
     * 软删除时间
     */
    var deletedAt: Instant? = null,

    //TODO 可添加 评论 IP，设备信息
)