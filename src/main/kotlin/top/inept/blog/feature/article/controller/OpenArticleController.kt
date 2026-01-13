package top.inept.blog.feature.article.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.base.PageResponse
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.feature.article.model.convert.toHomeArticleVO
import top.inept.blog.feature.article.model.dto.QueryArticleDTO
import top.inept.blog.feature.article.model.entity.enums.ArticleStatus
import top.inept.blog.feature.article.model.vo.HomeArticleVO
import top.inept.blog.feature.article.service.ArticleService

@Tag(name = "公开文章接口")
@RestController
@RequestMapping("/public/articles")
@Validated
class OpenArticleController(
    private val articleService: ArticleService,
) {
    @Operation(summary = "获取主页文章列表")
    @GetMapping
    fun getHomeArticles(@Valid queryArticleDTO: QueryArticleDTO): ResponseEntity<PageResponse<HomeArticleVO>> {
        val articlePage = articleService.getHomeArticles(queryArticleDTO.copy(articleStatus = ArticleStatus.Published))
        return ResponseEntity.ok(articlePage.toPageResponse { it.toHomeArticleVO() })
    }
}