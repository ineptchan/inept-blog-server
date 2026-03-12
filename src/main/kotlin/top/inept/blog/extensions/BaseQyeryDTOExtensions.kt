package top.inept.blog.extensions

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import top.inept.blog.base.BaseQueryDTO

fun BaseQueryDTO.toPageRequest(): Pageable = PageRequest.of(
    this.page,
    this.size
)

fun BaseQueryDTO.toPageRequest(sort: Sort): Pageable = PageRequest.of(
    this.page,
    this.size,
    sort
)
