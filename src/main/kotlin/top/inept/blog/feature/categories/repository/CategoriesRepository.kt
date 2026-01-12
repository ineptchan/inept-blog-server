package top.inept.blog.feature.categories.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import top.inept.blog.feature.categories.model.entity.Categories

@Repository
interface CategoriesRepository : JpaRepository<Categories, Long>, JpaSpecificationExecutor<Categories> {
    fun existsByName(name: String): Boolean
    fun existsBySlug(slug: String): Boolean
    fun existsByNameOrSlug(name: String, slug: String): Boolean

}