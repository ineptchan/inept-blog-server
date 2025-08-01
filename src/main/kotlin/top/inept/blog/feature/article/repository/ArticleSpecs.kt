package top.inept.blog.feature.article.repository

import org.springframework.data.jpa.domain.Specification
import top.inept.blog.feature.article.pojo.entity.Article
import top.inept.blog.feature.article.pojo.entity.Article_
import top.inept.blog.feature.article.pojo.entity.enums.ArticleStatus
import top.inept.blog.feature.categories.pojo.entity.Categories
import top.inept.blog.feature.categories.pojo.entity.Categories_
import top.inept.blog.feature.tag.pojo.entity.Tag
import top.inept.blog.feature.tag.pojo.entity.Tag_

object ArticleSpecs {
    fun hasCategory(category: Long?): Specification<Article>? {
        return category?.let {
            Specification<Article> { root, _, cb ->
                cb.equal(root.get<Categories>(Article_.category).get<Long>(Categories_.id), it)
            }
        }
    }

    fun hasTags(tags: List<Long>?): Specification<Article>? {
        return tags.takeIf { it != null && it.isNotEmpty() }?.let {
            Specification { root, query, cb ->
                query?.distinct(true)
                val tagsJoin = root.join<Article, Tag>("${Article_.tags}")
                tagsJoin.get<Long>(Tag_.id).`in`(it)
            }
        }
    }

    fun hasArticleStatus(articleStatus: ArticleStatus?): Specification<Article>? {
        return articleStatus?.let {
            Specification { root, _, cb ->
                cb.equal(root.get<ArticleStatus>(Article_.articleStatus), articleStatus)
            }
        }
    }

    fun titleContains(keyword: String?): Specification<Article>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get(Article_.title)), "%${it.lowercase()}%")
            }
        }
    }

    fun contentContains(keyword: String?): Specification<Article>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get(Article_.content)), "%${it.lowercase()}%")
            }
        }
    }

    fun titleOrContentContains(keyword: String?): Specification<Article>? {
        val titleSpec = titleContains(keyword)
        val contentSpec = contentContains(keyword)

        return when {
            titleSpec != null && contentSpec != null -> titleSpec.or(contentSpec)
            else -> titleSpec ?: contentSpec
        }
    }
}