package top.inept.blog.feature.article.repository

import org.springframework.data.jpa.domain.Specification
import top.inept.blog.feature.article.pojo.entity.Article
import top.inept.blog.feature.article.pojo.entity.enums.ArticleStatus
import top.inept.blog.feature.categories.pojo.entity.Categories
import top.inept.blog.feature.tag.pojo.entity.Tag

object ArticleSpecs {
    fun hasCategory(category: Long?): Specification<Article>? {
        return category?.let {
            Specification<Article> { root, _, cb ->
                cb.equal(root.get<Categories>("category").get<Long>("id"), it)
            }
        }
    }

    fun hasTags(tags: List<Long>?): Specification<Article>? {
        return tags.takeIf { it != null && it.isNotEmpty() }?.let {
            Specification { root, query, cb ->
                query?.distinct(true)
                val tagsJoin = root.join<Article, Tag>("tags")
                tagsJoin.get<Long>("id").`in`(it)
            }
        }
    }

    fun hasArticleStatus(articleStatus: ArticleStatus?): Specification<Article>? {
        return articleStatus?.let {
            Specification { root, _, cb ->
                cb.equal(root.get<ArticleStatus>("articleStatus"), articleStatus)
            }
        }
    }

    fun titleContains(keyword: String?): Specification<Article>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get("title")), "%${it.lowercase()}%")
            }
        }
    }

    fun contentContains(keyword: String?): Specification<Article>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get("content")), "%${it.lowercase()}%")
            }
        }
    }

    fun titleOrContentContains(titleKeyword: String?, contentKeyword: String?): Specification<Article>? {
        val titleSpec = titleContains(titleKeyword)
        val contentSpec = contentContains(contentKeyword)

        return when {
            titleSpec != null && contentSpec != null -> titleSpec.or(contentSpec)
            else -> titleSpec ?: contentSpec
        }
    }
}