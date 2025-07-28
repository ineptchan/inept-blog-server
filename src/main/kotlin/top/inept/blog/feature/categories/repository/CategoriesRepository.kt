package top.inept.blog.feature.categories.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import top.inept.blog.feature.categories.pojo.entity.Categories

@Repository
interface CategoriesRepository : JpaRepository<Categories, Long> {
    fun existsByName(name: String): Boolean
    fun existsBySlug(slug: String): Boolean
    fun existsByNameOrSlug(name: String, slug: String): Boolean

}