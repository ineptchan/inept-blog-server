package top.inept.blog.feature.comment.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.article.model.entity.Article
import top.inept.blog.feature.user.model.entity.User
import java.time.Instant

@Entity
@Table(name = "comments")
@EntityListeners(value = [AuditingEntityListener::class])
class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    var article: Article,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    var parentComment: Comment? = null,

    @OneToMany(
        mappedBy = "parentComment",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var replies: MutableList<Comment> = mutableListOf(),

    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),
)