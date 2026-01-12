package top.inept.blog.feature.article.repository

import org.springframework.data.jpa.domain.Specification
import top.inept.blog.feature.article.model.entity.Article
import top.inept.blog.feature.article.model.entity.Article_
import top.inept.blog.feature.article.model.entity.enums.ArticleStatus
import top.inept.blog.feature.categories.model.entity.Categories
import top.inept.blog.feature.categories.model.entity.Categories_
import top.inept.blog.feature.tag.model.entity.Tag
import top.inept.blog.feature.tag.model.entity.Tag_

object ArticleSpecs {
    fun byCategoryId(category: Long?): Specification<Article>? {
        return category?.let {
            Specification<Article> { root, _, cb ->
                cb.equal(root.get<Categories>(Article_.category).get<Long>(Categories_.id), it)
            }
        }
    }

    fun byTagIds(tags: List<Long>?): Specification<Article>? {
        return tags.takeIf { it != null && it.isNotEmpty() }?.let {
            Specification { root, query, cb ->
                query?.distinct(true)
                val tagsJoin = root.join<Article, Tag>("${Article_.tags}")
                tagsJoin.get<Long>(Tag_.id).`in`(it)
            }
        }
    }

    fun byArticleStatus(articleStatus: ArticleStatus?): Specification<Article>? {
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

    fun slugContains(keyword: String?): Specification<Article>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get(Article_.slug)), "%${it.lowercase()}%")
            }
        }
    }
}