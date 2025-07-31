package top.inept.blog.feature.article.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.base.ApiResponse
import top.inept.blog.base.PageResponse
import top.inept.blog.extensions.toApiResponse
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.feature.article.pojo.convert.toHomeArticleVO
import top.inept.blog.feature.article.pojo.dto.ArticleQueryDTO
import top.inept.blog.feature.article.pojo.entity.enums.ArticleStatus
import top.inept.blog.feature.article.pojo.vo.HomeArticleVO
import top.inept.blog.feature.article.service.ArticleService

@Tag(name = "公开文章接口")
@RestController
@RequestMapping("/open/articles")
@Validated
class OpenArticleController(
    private val articleService: ArticleService,
) {
    @Operation(summary = "获取主页文章列表")
    @GetMapping
    fun getHomeArticles(@Valid articleQueryDTO: ArticleQueryDTO): ApiResponse<PageResponse<HomeArticleVO>> {
        val articlePage = articleService.getHomeArticles(articleQueryDTO.copy(articleStatus = ArticleStatus.Published))
        return articlePage.toPageResponse { it.toHomeArticleVO() }.toApiResponse()
    }
}