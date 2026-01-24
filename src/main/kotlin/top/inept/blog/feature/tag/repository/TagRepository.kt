package top.inept.blog.feature.tag.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository
import top.inept.blog.feature.tag.model.entity.Tag

@Repository
interface TagRepository : JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag>,
    QuerydslPredicateExecutor<Tag> {
    fun existsByName(name: String): Boolean
    fun existsBySlug(slug: String): Boolean
    fun existsByNameOrSlug(name: String, slug: String): Boolean

}