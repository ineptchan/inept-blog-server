package top.inept.blog.feature.tag.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "tags")
class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false, unique = true)
    var slug: String,
)