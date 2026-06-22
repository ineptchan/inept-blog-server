package top.inept.blog.feature.article.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.feature.article.model.vo.LikeArticleVO
import top.inept.blog.feature.article.service.ArticleService

@Tag(name = "文章接口")
@RestController
@RequestMapping("/user/articles")
@Validated
class UserArticleController(
    private val articleService: ArticleService,
) {
    @PreAuthorize("hasAuthority('article:like')")
    @Operation(summary = "点赞文章")
    @PostMapping("/{id}/like")
    fun likeArticle(
        @Parameter(description = "openapi.article.id", required = true)
        @PathVariable id: Long
    ): ResponseEntity<LikeArticleVO> {
        return ResponseEntity.ok(LikeArticleVO(articleService.likeArticle(id)))
    }

    @PreAuthorize("hasAuthority('article:like')")
    @Operation(summary = "取消点赞文章")
    @DeleteMapping("/{id}/like")
    fun unlikeArticle(
        @Parameter(description = "openapi.article.id", required = true)
        @PathVariable id: Long
    ): ResponseEntity<LikeArticleVO> {
        return ResponseEntity.ok(LikeArticleVO(articleService.unlikeArticle(id)))
    }
}