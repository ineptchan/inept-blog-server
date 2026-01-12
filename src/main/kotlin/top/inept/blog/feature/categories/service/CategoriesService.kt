package top.inept.blog.feature.categories.service

import org.springframework.data.domain.Page
import top.inept.blog.feature.categories.model.dto.QueryCategoriesDTO
import top.inept.blog.feature.categories.model.dto.UpdateCategoriesDTO
import top.inept.blog.feature.categories.model.dto.CreateCategoriesDTO
import top.inept.blog.feature.categories.model.entity.Categories

interface CategoriesService {
    fun getCategories(queryCategoriesDTO: QueryCategoriesDTO): Page<Categories>
    fun getCategoriesById(id: Long): Categories
    fun createCategory(createCategoriesDTO: CreateCategoriesDTO): Categories
    fun updateCategory(updateCategoriesDTO: UpdateCategoriesDTO): Categories
    fun deleteCategory(id: Long)
}