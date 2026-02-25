package top.inept.blog.feature.tag.controller

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
import top.inept.blog.feature.tag.model.convert.toTagVO
import top.inept.blog.feature.tag.model.dto.CreateTagDTO
import top.inept.blog.feature.tag.model.dto.QueryTagDTO
import top.inept.blog.feature.tag.model.dto.UpdateTagDTO
import top.inept.blog.feature.tag.model.vo.TagVO
import top.inept.blog.feature.tag.service.TagService

@Tag(name = "标签接口")
@SecurityRequirement(name = "accessToken")
@RestController
@RequestMapping("/admin/tag")
@Validated
class AdminTagController(
    private val tagService: TagService,
) {
    @PreAuthorize("hasAuthority('admin:tag:read')")
    @Operation(summary = "获取标签列表")
    @GetMapping
    fun getTags(@Valid dto: QueryTagDTO): ResponseEntity<PageResponse<TagVO>> {
        return ResponseEntity.ok(tagService.getTags(dto).toPageResponse { it.toTagVO() })
    }

    @PreAuthorize("hasAuthority('admin:tag:read')")
    @Operation(summary = "根据id获取标签")
    @GetMapping("/{id}")
    fun getTagById(@PathVariable id: Long): ResponseEntity<TagVO> {
        return ResponseEntity.ok(tagService.getTagById(id).toTagVO())
    }

    @PreAuthorize("hasAuthority('admin:tag:write')")
    @Operation(summary = "创建标签")
    @PostMapping
    fun createTag(@Valid @RequestBody dto: CreateTagDTO): ResponseEntity<TagVO> {
        return ResponseEntity.ok(tagService.createTag(dto).toTagVO())
    }

    @PreAuthorize("hasAuthority('admin:tag:modify')")
    @Operation(summary = "更新标签")
    @PatchMapping("/{id}")
    fun updateTag(@PathVariable id: Long, @Valid @RequestBody dto: UpdateTagDTO): ResponseEntity<TagVO> {
        return ResponseEntity.ok(tagService.updateTag(id, dto).toTagVO())
    }

    @PreAuthorize("hasAuthority('admin:tag:delete')")
    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    fun deleteTag(@PathVariable id: Long): ResponseEntity<Boolean> {
        tagService.deleteTag(id)
        return ResponseEntity.ok(true)
    }
}