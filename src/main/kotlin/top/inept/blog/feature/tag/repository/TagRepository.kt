package top.inept.blog.feature.tag.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import top.inept.blog.feature.tag.pojo.entity.Tag

@Repository
interface TagRepository : JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {
    fun existsByName(name: String): Boolean
    fun existsBySlug(slug: String): Boolean
    fun existsByNameOrSlug(name: String, slug: String): Boolean

}