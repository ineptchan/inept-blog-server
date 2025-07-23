package top.inept.blog.feature.admin.categories.pojo.convert

import top.inept.blog.feature.admin.categories.pojo.dto.CategoriesDTO
import top.inept.blog.feature.admin.categories.pojo.entity.Categories
import top.inept.blog.feature.admin.categories.pojo.vo.CategoriesVO

fun CategoriesDTO.toCategories() = Categories(
    id = this.id,
    name = this.name,
    slug = this.slug
)

fun Categories.toCategoriesVO() = CategoriesVO(
    id = this.id,
    name = this.name,
    slug = this.slug
)