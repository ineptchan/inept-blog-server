package top.inept.blog.feature.admin.tag.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero
import org.hibernate.validator.constraints.Length

data class TagDTO(
    @Schema(description = "openapi.tag.id")
    @field:PositiveOrZero(message = "valid.common.id")
    val id: Long,

    @Schema(description = "openapi.tag.name")
    @Length(min = 2, max = 24, message = "valid.tag.name")
    val name: String,

    @Schema(description = "openapi.tag.slug")
    @field:Pattern(
        regexp = "^[a-z0-9]+(-[a-z0-9]+)$",
        message = "valid.tag.slug"
    )
    val slug: String,
)