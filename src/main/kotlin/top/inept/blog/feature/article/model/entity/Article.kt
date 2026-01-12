package top.inept.blog.feature.article.model.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.article.model.entity.enums.ArticleStatus
import top.inept.blog.feature.categories.model.entity.Categories
import top.inept.blog.feature.tag.pojo.entity.Tag
import top.inept.blog.feature.user.pojo.entity.User
import java.time.LocalDateTime

@Entity
@Table(name = "articles")
@EntityListeners(value = [AuditingEntityListener::class])
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, unique = true)
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

    //TODO EnumType.STRING
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    var articleStatus: ArticleStatus = ArticleStatus.Draft,

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
)