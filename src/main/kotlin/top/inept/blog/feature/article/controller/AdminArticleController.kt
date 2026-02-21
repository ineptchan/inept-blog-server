package top.inept.blog.feature.article.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
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
import top.inept.blog.feature.article.model.dto.*
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
    fun getArticles(@Valid queryArticleDTO: QueryArticleDTO): ResponseEntity<PageResponse<HomeArticleVO>> {
        val articlePage = articleService.getHomeArticles(queryArticleDTO)
        return ResponseEntity.ok(articlePage.toPageResponse { it.toHomeArticleVO() })
    }

    @PreAuthorize("hasAuthority('admin:article:read')")
    @Operation(summary = "根据id获取文章")
    @GetMapping("/{id}")
    fun getArticleById(@PathVariable id: Long): ResponseEntity<ArticleVO> {
        return ResponseEntity.ok(articleService.getArticleById(id).toArticleVO())
    }

    @PreAuthorize("hasAuthority('admin:article:write')")
    @Operation(summary = "创建文章")
    @PostMapping
    fun createArticle(@Valid @RequestBody createArticleDTO: CreateArticleDTO): ResponseEntity<ArticleVO> {
        return ResponseEntity.ok(articleService.createArticle(createArticleDTO).toArticleVO())
    }

    @PreAuthorize("hasAuthority('admin:article:modify')")
    @Operation(summary = "更新文章")
    @PatchMapping("/{id}")
    fun updateArticle(
        @Valid @RequestBody updateArticleDTO: UpdateArticleDTO,
        @PathVariable id: Long
    ): ResponseEntity<ArticleVO> {
        return ResponseEntity.ok(articleService.updateArticle(id, updateArticleDTO).toArticleVO())
    }

    @PreAuthorize("hasAuthority('admin:article:delete')")
    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    fun deleteArticle(@PathVariable id: Long): ResponseEntity<Boolean> {
        articleService.deleteArticle(id)
        return ResponseEntity.ok(true)
    }

    @PreAuthorize("hasAuthority('admin:article:modify')")
    @Operation(summary = "批量更新文章状态")
    @PutMapping("/status")
    fun updateArticleStatus(@Valid @RequestBody updateArticleStatusDTO: UpdateArticleStatusDTO): ResponseEntity<Boolean> {
        articleService.updateArticleStatus(updateArticleStatusDTO)
        return ResponseEntity.ok(true)
    }

    @PreAuthorize("hasAuthority('admin:article:write')")
    @Operation(summary = "上传文章图片")
    @PutMapping("/{id}/image")
    fun uploadImage(@PathVariable id: Long, @Valid @ModelAttribute dto: UploadArticleImageDTO): ResponseEntity<String> {
        return ResponseEntity.ok(articleService.uploadImage(id, dto))
    }

    //TODO 添加更换作者接口
}