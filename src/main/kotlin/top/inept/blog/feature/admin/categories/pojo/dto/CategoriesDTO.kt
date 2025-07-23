package top.inept.blog.feature.admin.categories.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero
import org.hibernate.validator.constraints.Length

data class CategoriesDTO(
    @Schema(description = "openapi.categories.id")
    @field:PositiveOrZero(message = "valid.common.id")
    val id: Long,

    @Schema(description = "openapi.categories.name")
    @Length(min = 2, max = 24, message = "valid.categories.name")
    val name: String,

    @Schema(description = "openapi.categories.slug")
    @field:Pattern(
        regexp = "^[a-z0-9]+(-[a-z0-9]+)$",
        message = "valid.categories.slug"
    )
    val slug: String,
)