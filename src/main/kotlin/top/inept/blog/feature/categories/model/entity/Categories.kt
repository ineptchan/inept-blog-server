package top.inept.blog.feature.categories.model.entity

import jakarta.persistence.*
import top.inept.blog.feature.categories.model.entity.constraints.CategoriesConstraints

@Entity
@Table(
    name = "categories",
    uniqueConstraints = [
        UniqueConstraint(name = CategoriesConstraints.UNIQUE_NAME, columnNames = ["name"]),
        UniqueConstraint(name = CategoriesConstraints.UNIQUE_SLUG, columnNames = ["slug"]),
    ]
)
class Categories(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "slug", nullable = false)
    var slug: String,
)