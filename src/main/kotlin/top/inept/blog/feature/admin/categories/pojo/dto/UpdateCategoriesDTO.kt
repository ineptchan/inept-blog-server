package top.inept.blog.feature.admin.categories.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.PositiveOrZero
import top.inept.blog.feature.admin.categories.pojo.validated.ValidatedCategoriesName
import top.inept.blog.feature.admin.categories.pojo.validated.ValidatedCategoriesSlug

data class UpdateCategoriesDTO(
    @Schema(description = "openapi.categories.id")
    @field:PositiveOrZero(message = "valid.common.id")
    val id: Long,

    @Schema(description = "openapi.categories.name")
    @field:ValidatedCategoriesName
    val name: String,

    @Schema(description = "openapi.categories.slug")
    @field:ValidatedCategoriesSlug
    val slug: String,
)