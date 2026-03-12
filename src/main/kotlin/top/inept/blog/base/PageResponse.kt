package top.inept.blog.base

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Pageable

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
) {
    companion object {
        fun <T> of(content: List<T>, pageable: Pageable, totalElements: Long): PageResponse<T> {
            val size = pageable.pageSize
            val totalPages = if (size == 0) 0 else ((totalElements + size - 1) / size).toInt()

            return PageResponse(
                content = content,
                page = pageable.pageNumber + 1,
                size = size,
                totalElements = totalElements,
                totalPages = totalPages
            )
        }

        fun <T> empty(pageable: Pageable): PageResponse<T> =
            of(emptyList(), pageable, 0)
    }
}