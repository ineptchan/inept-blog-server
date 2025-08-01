package top.inept.blog.feature.article.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.ApiResponse
import top.inept.blog.base.PageResponse
import top.inept.blog.extensions.toApiResponse
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.feature.article.pojo.convert.toArticleVO
import top.inept.blog.feature.article.pojo.convert.toHomeArticleVO
import top.inept.blog.feature.article.pojo.dto.ArticleQueryDTO
import top.inept.blog.feature.article.pojo.dto.CreateArticleDTO
import top.inept.blog.feature.article.pojo.dto.UpdateArticleDTO
import top.inept.blog.feature.article.pojo.dto.UpdateArticleStatusDTO
import top.inept.blog.feature.article.pojo.vo.ArticleVO
import top.inept.blog.feature.article.pojo.vo.HomeArticleVO
import top.inept.blog.feature.article.service.ArticleService

@Tag(name = "管理员文章接口")
@RestController
@RequestMapping("/admin/articles")
@Validated
class AdminArticleController(
    private val articleService: ArticleService,
) {
    @Operation(summary = "获取文章列表")
    @GetMapping
    fun getArticles(@Valid articleQueryDTO: ArticleQueryDTO): ApiResponse<PageResponse<HomeArticleVO>> {
        val articlePage = articleService.getHomeArticles(articleQueryDTO)
        return articlePage.toPageResponse { it.toHomeArticleVO() }.toApiResponse()
    }

    @Operation(summary = "根据id获取文章")
    @GetMapping("/{id}")
    fun getArticleById(@PathVariable id: Long): ApiResponse<ArticleVO> {
        return ApiResponse.success(articleService.getArticleById(id).toArticleVO())
    }

    @Operation(summary = "创建文章")
    @PostMapping
    fun createArticle(@Valid @RequestBody createArticleDTO: CreateArticleDTO): ApiResponse<ArticleVO> {
        return ApiResponse.success(articleService.createArticle(createArticleDTO).toArticleVO())
    }

    @Operation(summary = "更新文章")
    @PutMapping
    fun updateArticle(@Valid @RequestBody updateArticleDTO: UpdateArticleDTO): ApiResponse<ArticleVO> {
        return ApiResponse.success(articleService.updateArticle(updateArticleDTO).toArticleVO())
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    fun deleteArticle(@PathVariable id: Long): ApiResponse<Boolean> {
        articleService.deleteArticle(id)
        return ApiResponse.success(true)
    }

    @Operation(summary = "批量更新文章状态")
    @PutMapping("/status")
    fun updateArticleStatus(@Valid @RequestBody updateArticleStatusDTO: UpdateArticleStatusDTO): ApiResponse<Boolean> {
        articleService.updateArticleStatus(updateArticleStatusDTO)
        return ApiResponse.success(true)
    }

    //TODO 添加更换作者接口
}