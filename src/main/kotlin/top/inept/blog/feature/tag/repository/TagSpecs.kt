package top.inept.blog.feature.tag.repository

import org.springframework.data.jpa.domain.Specification
import top.inept.blog.feature.tag.pojo.entity.Tag
import top.inept.blog.feature.tag.pojo.entity.Tag_

object TagSpecs {
    fun nameContains(keyword: String?): Specification<Tag>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get(Tag_.name)), "%${it.lowercase()}%")
            }
        }
    }

    fun slugContains(keyword: String?): Specification<Tag>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get(Tag_.slug)), "%${it.lowercase()}%")
            }
        }
    }

    fun nameOrSlugContains(keyword: String?): Specification<Tag>? {
        val nameSpec = nameContains(keyword)
        val slugSpec = slugContains(keyword)

        return when {
            nameSpec != null && slugSpec != null -> nameSpec.or(slugSpec)
            else -> nameSpec ?: slugSpec
        }
    }
}