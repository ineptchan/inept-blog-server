package top.inept.blog.feature.tag.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.feature.tag.model.validated.ValidatedTagName
import top.inept.blog.feature.tag.model.validated.ValidatedTagSlug

data class CreateTagDTO(
    @field:Schema(description = "openapi.tag.name")
    @field:ValidatedTagName
    val name: String,

    @field:Schema(description = "openapi.tag.slug")
    @field:ValidatedTagSlug
    val slug: String,
)