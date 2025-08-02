package top.inept.blog.extensions

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import top.inept.blog.base.BaseQueryDTO

fun BaseQueryDTO.toPageRequest() = PageRequest.of(
    this.page - 1,
    this.size
)

fun BaseQueryDTO.toPageRequest(sort: Sort) = PageRequest.of(
    this.page - 1,
    this.size,
    sort
)
