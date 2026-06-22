package top.inept.blog.feature.article.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
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

@Tag(name = "文章接口")
@SecurityRequirement(name = "accessToken")
@RestController
@RequestMapping("/admin/articles")
@Validated
class AdminArticleController(
    private val articleService: ArticleService,
) {
    @PreAuthorize("hasAuthority('admin:article:read')")
    @Operation(summary = "获取文章列表")
    @GetMapping
    fun getArticles(
        @Valid
        @ParameterObject
        @ModelAttribute
        dto: QueryArticleDTO
    ): ResponseEntity<PageResponse<HomeArticleVO>> {
        val articlePage = articleService.getHomeArticles(dto)
        return ResponseEntity.ok(articlePage.toPageResponse { it.toHomeArticleVO() })
    }

    @PreAuthorize("hasAuthority('admin:article:read')")
    @Operation(summary = "根据id获取文章")
    @GetMapping("/{id}")
    fun getArticleById(
        @Parameter(description = "openapi.article.id", required = true)
        @PathVariable id: Long
    ): ResponseEntity<ArticleVO> {
        return ResponseEntity.ok(articleService.getArticleById(id).toArticleVO())
    }

    @PreAuthorize("hasAuthority('admin:article:create')")
    @Operation(summary = "创建文章")
    @PostMapping
    fun createArticle(@Valid @RequestBody dto: CreateArticleDTO): ResponseEntity<ArticleVO> {
        return ResponseEntity.ok(articleService.createArticle(dto).toArticleVO())
    }

    @PreAuthorize("hasAuthority('admin:article:update')")
    @Operation(summary = "更新文章")
    @PatchMapping("/{id}")
    fun updateArticle(
        @Parameter(description = "openapi.article.id", required = true)
        @PathVariable id: Long,
        @Valid @RequestBody dto: UpdateArticleDTO
    ): ResponseEntity<ArticleVO> {
        return ResponseEntity.ok(articleService.updateArticle(id, dto).toArticleVO())
    }

    @PreAuthorize("hasAuthority('admin:article:delete')")
    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    fun deleteArticle(
        @Parameter(description = "openapi.article.id", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Boolean> {
        articleService.deleteArticle(id)
        return ResponseEntity.ok(true)
    }

    @PreAuthorize("hasAuthority('admin:article:update')")
    @Operation(summary = "批量更新文章状态")
    @PutMapping("/status")
    fun updateArticleStatus(@Valid @RequestBody dto: UpdateArticleStatusDTO): ResponseEntity<Boolean> {
        articleService.updateArticleStatus(dto)
        return ResponseEntity.ok(true)
    }

    //TODO 添加更换作者接口
}