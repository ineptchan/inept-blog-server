package top.inept.blog.feature.article.model.vo

import io.swagger.v3.oas.annotations.media.Schema

data class LikeArticleVO(
    @field:Schema(description = "openapi.article.like", example = "12")
    val likeCount: Long
)