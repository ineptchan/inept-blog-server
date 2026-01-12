package top.inept.blog.feature.categories.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class CategoriesVO (
    @Schema(description = "openapi.categories.id")
    val id: Long,

    @Schema(description = "openapi.categories.name")
    val name: String,

    @Schema(description = "openapi.categories.slug")
    val slug: String
)