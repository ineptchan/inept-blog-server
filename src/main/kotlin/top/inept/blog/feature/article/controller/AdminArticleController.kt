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
    fun getArticles(@Valid dto: QueryArticleDTO): ResponseEntity<PageResponse<HomeArticleVO>> {
        val articlePage = articleService.getHomeArticles(dto)
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
    fun createArticle(@Valid @RequestBody dto: CreateArticleDTO): ResponseEntity<ArticleVO> {
        return ResponseEntity.ok(articleService.createArticle(dto).toArticleVO())
    }

    @PreAuthorize("hasAuthority('admin:article:modify')")
    @Operation(summary = "更新文章")
    @PatchMapping("/{id}")
    fun updateArticle(@PathVariable id: Long, @Valid @RequestBody dto: UpdateArticleDTO): ResponseEntity<ArticleVO> {
        return ResponseEntity.ok(articleService.updateArticle(id, dto).toArticleVO())
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
    fun updateArticleStatus(@Valid @RequestBody dto: UpdateArticleStatusDTO): ResponseEntity<Boolean> {
        articleService.updateArticleStatus(dto)
        return ResponseEntity.ok(true)
    }

    @PreAuthorize("hasAuthority('admin:article:write')")
    @Operation(summary = "上传文章图片")
    @PutMapping("/{id}/image")
    fun uploadImage(@PathVariable id: Long, @Valid @ModelAttribute dto: UploadArticleImageDTO): ResponseEntity<String> {
        return ResponseEntity.ok(articleService.uploadImage(id, dto))
    }

    @PreAuthorize("hasAuthority('admin:article:write')")
    @Operation(summary = "上传文章封面图片")
    @PutMapping("/{id}/featured-image")
    fun uploadFeaturedImage(
        @PathVariable id: Long,
        @Valid @ModelAttribute dto: UploadArticleFeaturedImageDTO
    ): ResponseEntity<String> {
        return ResponseEntity.ok(articleService.uploadFeaturedImage(id, dto))
    }

    @PreAuthorize("hasAuthority('admin:article:write')")
    @Operation(summary = "上传文章视频")
    @PutMapping("/{id}/video")
    fun uploadVideo(@PathVariable id: Long, @Valid @ModelAttribute dto: UploadArticleVideoDTO): ResponseEntity<String> {
        return ResponseEntity.ok(articleService.uploadVideo(id, dto))
    }

    @PreAuthorize("hasAuthority('admin:article:write')")
    @Operation(summary = "上传文章附件")
    @PutMapping("/{id}/attachment")
    fun uploadAttachment(
        @PathVariable id: Long,
        @Valid @ModelAttribute dto: UploadArticleAttachmentDTO
    ): ResponseEntity<String> {
        return ResponseEntity.ok(articleService.uploadAttachment(id, dto))
    }

    //TODO 添加更换作者接口
}