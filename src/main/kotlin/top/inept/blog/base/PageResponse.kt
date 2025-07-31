package top.inept.blog.base

import io.swagger.v3.oas.annotations.media.Schema

data class PageResponse<T>(
    @Schema(description = "openapi.response.content")
    val content: List<T>,

    @Schema(description = "openapi.response.page")
    val page: Int,

    @Schema(description = "openapi.response.size")
    val size: Int,

    @Schema(description = "openapi.response.total_elements")
    val totalElements: Long,

    @Schema(description = "openapi.response.total_pages")
    val totalPages: Int
)