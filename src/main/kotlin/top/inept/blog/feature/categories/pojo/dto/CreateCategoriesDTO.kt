package top.inept.blog.feature.categories.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.categories.pojo.validated.ValidatedCategoriesName
import top.inept.blog.feature.categories.pojo.validated.ValidatedCategoriesSlug

data class CreateCategoriesDTO(
    @Schema(description = "openapi.categories.name")
    @field:ValidatedCategoriesName
    val name: String,

    @Schema(description = "openapi.categories.slug")
    @field:ValidatedCategoriesSlug
    val slug: String,
)