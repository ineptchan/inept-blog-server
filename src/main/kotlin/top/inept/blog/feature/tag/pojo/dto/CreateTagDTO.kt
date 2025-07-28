package top.inept.blog.feature.tag.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.tag.pojo.validated.ValidatedTagName
import top.inept.blog.feature.tag.pojo.validated.ValidatedTagSlug

data class CreateTagDTO(
    @Schema(description = "openapi.tag.name")
    @field:ValidatedTagName
    val name: String,

    @Schema(description = "openapi.tag.slug")
    @field:ValidatedTagSlug
    val slug: String,
)