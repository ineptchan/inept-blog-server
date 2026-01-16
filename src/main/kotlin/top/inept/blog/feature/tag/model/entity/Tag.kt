package top.inept.blog.feature.tag.model.entity

import jakarta.persistence.*
import top.inept.blog.feature.tag.model.entity.constraints.TagConstraints

@Entity
@Table(
    name = "tags",
    uniqueConstraints = [
        UniqueConstraint(name = TagConstraints.UNIQUE_NAME, columnNames = ["name"]),
        UniqueConstraint(name = TagConstraints.UNIQUE_SLUG, columnNames = ["slug"]),
    ]
)
class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name", nullable = false, unique = true)
    var name: String,

    @Column(name = "slug", nullable = false, unique = true)
    var slug: String,
)