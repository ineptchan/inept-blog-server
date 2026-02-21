package top.inept.blog.feature.article.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.article.model.entity.constraints.ArticleConstraints
import top.inept.blog.feature.article.model.entity.enums.ArticleStatus
import top.inept.blog.feature.categories.model.entity.Categories
import top.inept.blog.feature.objectstorage.model.entity.ObjectStorage
import top.inept.blog.feature.tag.model.entity.Tag
import top.inept.blog.feature.user.model.entity.User
import java.time.Instant

@Entity
@Table(
    name = "articles",
    uniqueConstraints = [
        UniqueConstraint(name = ArticleConstraints.UNIQUE_SLUG, columnNames = ["slug"])
    ]
)
@EntityListeners(value = [AuditingEntityListener::class])
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var title: String,

    @Column(name = "slug", nullable = false)
    var slug: String,

    //@Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    var author: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: Categories,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "article_tags",
        joinColumns = [JoinColumn(name = "article_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var articleStatus: ArticleStatus = ArticleStatus.DRAFT,

    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),

    @LastModifiedDate
    var updatedAt: Instant? = null,

    @OneToMany(mappedBy = "ownerArticle", orphanRemoval = true)
    var objectStorages: MutableSet<ObjectStorage> = mutableSetOf(),
)