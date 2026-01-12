package top.inept.blog.feature.comment.model.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.article.model.entity.Article
import top.inept.blog.feature.user.pojo.entity.User
import java.time.LocalDateTime

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

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
)