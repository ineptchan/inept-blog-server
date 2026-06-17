package top.inept.blog.feature.article.model.entity

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import top.inept.blog.feature.article.model.entity.constraints.ArticleObjectStorageConstraints
import top.inept.blog.feature.objectstorage.model.entity.ObjectStorage

@Entity
@Table(
    name = "article_object_storage_table",
    uniqueConstraints = [
        UniqueConstraint(
            name = ArticleObjectStorageConstraints.UNIQUE_ARTICLE_OBJECT_STORAGE,
            columnNames = ["article_id", "object_storage_id"]
        )
    ]
)
@EntityListeners(value = [AuditingEntityListener::class])
class ArticleObjectStorage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    var article: Article,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_storage_id", nullable = false)
    var objectStorage: ObjectStorage,
)