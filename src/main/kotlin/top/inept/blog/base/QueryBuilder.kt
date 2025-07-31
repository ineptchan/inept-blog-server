package top.inept.blog.base

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification

data class QueryBuilder<T>(
    val specs: MutableList<Specification<T>> = mutableListOf(),
    var sort: Sort = Sort.unsorted()
) {

    fun and(spec: Specification<T>?): QueryBuilder<T> {
        if (spec != null) specs.add(spec)
        return this
    }

    fun orderByAsc(field: String): QueryBuilder<T> {
        sort = Sort.by(Sort.Direction.ASC, field)
        return this
    }

    fun orderByDesc(field: String): QueryBuilder<T> {
        sort = Sort.by(Sort.Direction.DESC, field)
        return this
    }

    fun buildSpec(): Specification<T>? =
        specs.reduceOrNull(Specification<T>::and)
}