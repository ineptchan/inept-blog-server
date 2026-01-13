package top.inept.blog.feature.article.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.PageResponse
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.feature.article.model.convert.toArticleVO
import top.inept.blog.feature.article.model.convert.toHomeArticleVO
import top.inept.blog.feature.article.model.dto.CreateArticleDTO
import top.inept.blog.feature.article.model.dto.QueryArticleDTO
import top.inept.blog.feature.article.model.dto.UpdateArticleDTO
import top.inept.blog.feature.article.model.dto.UpdateArticleStatusDTO
import top.inept.blog.feature.article.model.vo.ArticleVO
import top.inept.blog.feature.article.model.vo.HomeArticleVO
import top.inept.blog.feature.article.service.ArticleService

@Tag(name = "管理员文章接口")
@RestController
@RequestMapping("/admin/articles")
@Validated
class AdminArticleController(
    private val articleService: ArticleService,
) {
    @PreAuthorize("hasAuthority('admin:articles:read')")
    @Operation(summary = "获取文章列表")
    @GetMapping
    fun getArticles(@Valid queryArticleDTO: QueryArticleDTO): ResponseEntity<PageResponse<HomeArticleVO>> {
        val articlePage = articleService.getHomeArticles(queryArticleDTO)
        return ResponseEntity.ok(articlePage.toPageResponse { it.toHomeArticleVO() })
    }

    @Operation(summary = "根据id获取文章")
    @GetMapping("/{id}")
    fun getArticleById(@PathVariable id: Long): ResponseEntity<ArticleVO> {
        return ResponseEntity.ok(articleService.getArticleById(id).toArticleVO())
    }

    @Operation(summary = "创建文章")
    @PostMapping
    fun createArticle(@Valid @RequestBody createArticleDTO: CreateArticleDTO): ResponseEntity<ArticleVO> {
        return ResponseEntity.ok(articleService.createArticle(createArticleDTO).toArticleVO())
    }

    @Operation(summary = "更新文章")
    @PutMapping
    fun updateArticle(@Valid @RequestBody updateArticleDTO: UpdateArticleDTO): ResponseEntity<ArticleVO> {
        return ResponseEntity.ok(articleService.updateArticle(updateArticleDTO).toArticleVO())
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    fun deleteArticle(@PathVariable id: Long): ResponseEntity<Boolean> {
        articleService.deleteArticle(id)
        return ResponseEntity.ok(true)
    }

    @Operation(summary = "批量更新文章状态")
    @PutMapping("/status")
    fun updateArticleStatus(@Valid @RequestBody updateArticleStatusDTO: UpdateArticleStatusDTO): ResponseEntity<Boolean> {
        articleService.updateArticleStatus(updateArticleStatusDTO)
        return ResponseEntity.ok(true)
    }

    //TODO 添加更换作者接口
}