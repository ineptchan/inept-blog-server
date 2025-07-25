package top.inept.blog.feature.admin.tag.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.PositiveOrZero
import top.inept.blog.feature.admin.tag.pojo.validated.ValidatedTagName
import top.inept.blog.feature.admin.tag.pojo.validated.ValidatedTagSlug

data class UpdateTagDTO(
    @Schema(description = "openapi.tag.id")
    @field:PositiveOrZero(message = "valid.common.id")
    val id: Long,

    @Schema(description = "openapi.tag.name")
    @field:ValidatedTagName
    val name: String,

    @Schema(description = "openapi.tag.slug")
    @field:ValidatedTagSlug
    val slug: String,
)