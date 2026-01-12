package top.inept.blog.feature.categories.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.categories.model.validated.ValidatedCategoriesName
import top.inept.blog.feature.categories.model.validated.ValidatedCategoriesSlug

data class CreateCategoriesDTO(
    @Schema(description = "openapi.categories.name")
    @field:ValidatedCategoriesName
    val name: String,

    @Schema(description = "openapi.categories.slug")
    @field:ValidatedCategoriesSlug
    val slug: String,
)