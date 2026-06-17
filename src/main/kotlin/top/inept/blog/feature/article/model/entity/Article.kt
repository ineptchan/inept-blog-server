package top.inept.blog.feature.article.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.article.model.entity.constraints.ArticleConstraints
import top.inept.blog.feature.article.model.entity.enums.ArticleStatus
import top.inept.blog.feature.categories.model.entity.Categories
import top.inept.blog.feature.tag.model.entity.Tag
import top.inept.blog.feature.user.model.entity.User
import java.time.Instant

@Entity
@Table(
    name = "article_table",
    uniqueConstraints = [
        UniqueConstraint(name = ArticleConstraints.UNIQUE_SLUG, columnNames = ["slug"])
    ]
)
@EntityListeners(value = [AuditingEntityListener::class])
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "slug", nullable = false)
    var slug: String,

    @Column(name = "featured_image")
    var featuredImage: String? = null,

    //@Basic(fetch = FetchType.LAZY)
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    var author: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Categories,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var articleStatus: ArticleStatus = ArticleStatus.DRAFT,

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),

    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "article_tag_table",
        joinColumns = [JoinColumn(name = "article_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf(),

//    @OneToMany(mappedBy = "ownerArticle", orphanRemoval = true)
//    var objectStorages: MutableSet<ObjectStorage> = mutableSetOf(),
)