package top.inept.blog.feature.categories.service

import org.springframework.data.domain.Page
import top.inept.blog.feature.categories.model.dto.CreateCategoriesDTO
import top.inept.blog.feature.categories.model.dto.QueryCategoriesDTO
import top.inept.blog.feature.categories.model.dto.UpdateCategoriesDTO
import top.inept.blog.feature.categories.model.entity.Categories

interface CategoriesService {
    fun getCategories(dto: QueryCategoriesDTO): Page<Categories>
    fun getCategoriesById(id: Long): Categories
    fun createCategory(dto: CreateCategoriesDTO): Categories
    fun updateCategory(id: Long, dto: UpdateCategoriesDTO): Categories
    fun deleteCategory(id: Long)
}