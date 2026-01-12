package top.inept.blog.feature.categories.repository

import org.springframework.data.jpa.domain.Specification
import top.inept.blog.feature.categories.model.entity.Categories
import top.inept.blog.feature.categories.model.entity.Categories_

object CategoriesSpecs {
    fun nameContains(keyword: String?): Specification<Categories>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get(Categories_.name)), "%${it.lowercase()}%")
            }
        }
    }

    fun slugContains(keyword: String?): Specification<Categories>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get(Categories_.slug)), "%${it.lowercase()}%")
            }
        }
    }
}