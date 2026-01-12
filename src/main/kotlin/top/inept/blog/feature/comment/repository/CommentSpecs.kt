package top.inept.blog.feature.comment.repository

import org.springframework.data.jpa.domain.Specification
import top.inept.blog.feature.article.model.entity.Article_
import top.inept.blog.feature.comment.model.entity.Comment
import top.inept.blog.feature.comment.model.entity.Comment_

object CommentSpecs {
    fun contentContains(keyword: String?): Specification<Comment>? {
        return keyword?.takeIf { it.isNotBlank() }?.let {
            Specification { root, _, cb ->
                cb.like(cb.lower(root.get(Comment_.content)), "%${it.lowercase()}%")
            }
        }
    }

    fun byParentComment(comment: Comment): Specification<Comment> {
        return Specification { root, _, cb ->
            cb.equal(root.get(Comment_.parentComment), comment)
        }
    }

    fun byArticleId(articleId: Long): Specification<Comment> {
        return Specification { root, _, cb ->
            cb.equal(root.get(Comment_.article).get(Article_.id), articleId)
        }
    }

    fun isRootComment(): Specification<Comment>{
        return Specification { root, _, cb ->
            cb.isNull(root.get(Comment_.parentComment))
        }
    }
}