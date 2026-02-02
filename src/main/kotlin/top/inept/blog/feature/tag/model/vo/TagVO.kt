package top.inept.blog.feature.tag.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class TagVO(
    @field:Schema(description = "openapi.tag.id")
    val id: Long,

    @field:Schema(description = "openapi.tag.name")
    val name: String,

    @field:Schema(description = "openapi.tag.slug")
    val slug: String
)