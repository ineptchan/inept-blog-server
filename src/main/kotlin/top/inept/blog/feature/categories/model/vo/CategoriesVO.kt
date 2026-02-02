package top.inept.blog.feature.categories.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class CategoriesVO (
    @field:Schema(description = "openapi.categories.id")
    val id: Long,

    @field:Schema(description = "openapi.categories.name")
    val name: String,

    @field:Schema(description = "openapi.categories.slug")
    val slug: String
)