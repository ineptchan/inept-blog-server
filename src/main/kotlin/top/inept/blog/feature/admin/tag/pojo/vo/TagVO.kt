package top.inept.blog.feature.admin.tag.pojo.vo

import io.swagger.v3.oas.annotations.media.Schema

data class TagVO(
    @Schema(description = "openapi.tag.id")
    val id: Long,

    @Schema(description = "openapi.tag.name")
    val name: String,

    @Schema(description = "openapi.tag.slug")
    val slug: String
)