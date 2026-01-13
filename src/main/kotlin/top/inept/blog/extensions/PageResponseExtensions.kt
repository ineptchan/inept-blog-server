package top.inept.blog.extensions

import org.springframework.data.domain.Page
import top.inept.blog.base.ApiResponse
import top.inept.blog.base.PageResponse

fun <T> PageResponse<T>.toApiResponse(): ApiResponse<PageResponse<T>> {
    return ApiResponse(
        code = 0,
        msg = "success",
        data = this
    )
}

fun <T : Any> Page<T>.toPageResponse(): PageResponse<T> {
    return PageResponse(
        content = this.content,
        page = this.number + 1,
        size = this.size,
        totalElements = this.totalElements,
        totalPages = this.totalPages
    )
}

fun <E : Any, V> Page<E>.toPageResponse(transform: (E) -> V): PageResponse<V> {
    return PageResponse(
        content = this.content.map(transform),
        page = this.number + 1,
        size = this.size,
        totalElements = this.totalElements,
        totalPages = this.totalPages
    )
}

fun <E : Any, V> Page<E>.toPageResponseTransformNotNull(transform: (E) -> V?): PageResponse<V> {
    return PageResponse(
        content = this.content.mapNotNull(transform),
        page = this.number + 1,
        size = this.size,
        totalElements = this.totalElements,
        totalPages = this.totalPages
    )
}