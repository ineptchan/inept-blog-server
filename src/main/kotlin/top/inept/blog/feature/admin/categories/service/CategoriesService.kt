package top.inept.blog.feature.admin.categories.service

import top.inept.blog.feature.admin.categories.pojo.entity.Categories

interface CategoriesService {
    fun getCategories(): List<Categories>
    fun getCategoriesById(id: Long): Categories
    fun createCategory(categories: Categories): Categories
    fun updateCategory(categories: Categories): Categories
    fun deleteCategory(id: Long)
}