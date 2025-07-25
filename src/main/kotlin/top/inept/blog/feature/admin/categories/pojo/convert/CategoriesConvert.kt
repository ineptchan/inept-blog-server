package top.inept.blog.feature.admin.categories.pojo.convert

import top.inept.blog.feature.admin.categories.pojo.dto.UpdateCategoriesDTO
import top.inept.blog.feature.admin.categories.pojo.dto.CreateCategoriesDTO
import top.inept.blog.feature.admin.categories.pojo.entity.Categories
import top.inept.blog.feature.admin.categories.pojo.vo.CategoriesVO

fun Categories.toCategoriesVO() = CategoriesVO(
    id = this.id,
    name = this.name,
    slug = this.slug
)

fun UpdateCategoriesDTO.toCategories() = Categories(
    id = this.id,
    name = this.name,
    slug = this.slug
)

fun CreateCategoriesDTO.toCategories() = Categories(
    name = this.name,
    slug = this.slug
)