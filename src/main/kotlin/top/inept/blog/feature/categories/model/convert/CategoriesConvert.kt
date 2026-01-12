package top.inept.blog.feature.categories.model.convert

import top.inept.blog.feature.categories.model.dto.UpdateCategoriesDTO
import top.inept.blog.feature.categories.model.dto.CreateCategoriesDTO
import top.inept.blog.feature.categories.model.entity.Categories
import top.inept.blog.feature.categories.model.vo.CategoriesVO

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