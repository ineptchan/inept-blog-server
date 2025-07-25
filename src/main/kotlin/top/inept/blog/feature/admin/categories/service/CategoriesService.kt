package top.inept.blog.feature.admin.categories.service

import top.inept.blog.feature.admin.categories.pojo.dto.UpdateCategoriesDTO
import top.inept.blog.feature.admin.categories.pojo.dto.CreateCategoriesDTO
import top.inept.blog.feature.admin.categories.pojo.entity.Categories

interface CategoriesService {
    fun getCategories(): List<Categories>
    fun getCategoriesById(id: Long): Categories
    fun createCategory(createCategoriesDTO: CreateCategoriesDTO): Categories
    fun updateCategory(updateCategoriesDTO: UpdateCategoriesDTO): Categories
    fun deleteCategory(id: Long)
}