package top.inept.blog.feature.admin.categories.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import top.inept.blog.feature.admin.categories.pojo.entity.Categories

@Repository
interface CategoriesRepository : JpaRepository<Categories, Long> {
    fun existsByName(name: String): Boolean

}