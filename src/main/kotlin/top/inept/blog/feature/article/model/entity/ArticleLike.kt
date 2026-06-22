package top.inept.blog.feature.article.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.article.model.entity.constraints.ArticleLikeConstraints
import top.inept.blog.feature.user.model.entity.User
import java.time.Instant

@Entity
@Table(
    name = "article_likes_table",
    uniqueConstraints = [
        UniqueConstraint(name = ArticleLikeConstraints.UNIQUE_ARTICLE_USER, columnNames = ["article_id", "user_id"])
    ]
)
@EntityListeners(value = [AuditingEntityListener::class])
class ArticleLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    var article: Article,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),
)