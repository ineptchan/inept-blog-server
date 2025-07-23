package top.inept.blog.feature.admin.categories.pojo.entity

import jakarta.persistence.*

@Entity
@Table(name = "categories")
class Categories(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false, unique = true)
    var slug: String,
)