package top.inept.blog.base

import io.swagger.v3.oas.annotations.media.Schema

data class PageResponse<T>(
    @field:Schema(description = "openapi.response.content")
    val content: List<T>,

    @field:Schema(description = "openapi.response.page")
    val page: Int,

    @field:Schema(description = "openapi.response.size")
    val size: Int,

    @field:Schema(description = "openapi.response.total_elements")
    val totalElements: Long,

    @field:Schema(description = "openapi.response.total_pages")
    val totalPages: Int
)