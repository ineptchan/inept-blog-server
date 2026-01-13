package top.inept.blog.feature.tag.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
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

@Tag(name = "管理员标签接口")
@RestController
@RequestMapping("/admin/tag")
@Validated
class AdminTagController(
    private val tagService: TagService,
) {
    @Operation(summary = "获取标签列表")
    @GetMapping()
    fun getTags(@Valid queryTagDTO: QueryTagDTO): ResponseEntity<PageResponse<TagVO>> {
        return ResponseEntity.ok(
            tagService
                .getTags(queryTagDTO)
                .toPageResponse { it.toTagVO() }
        )
    }

    @Operation(summary = "根据id获取标签")
    @GetMapping("/{id}")
    fun getTagById(@PathVariable id: Long): ResponseEntity<TagVO> {
        return ResponseEntity.ok(tagService.getTagById(id).toTagVO())
    }

    @Operation(summary = "创建标签")
    @PostMapping
    fun createTag(@Valid @RequestBody createTagDTO: CreateTagDTO): ResponseEntity<TagVO> {
        return ResponseEntity.ok(tagService.createTag(createTagDTO).toTagVO())
    }

    @Operation(summary = "更新标签")
    @PutMapping
    fun updateTag(@Valid @RequestBody updateTagDTO: UpdateTagDTO): ResponseEntity<TagVO> {
        return ResponseEntity.ok(tagService.updateTag(updateTagDTO).toTagVO())
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    fun deleteTag(@PathVariable id: Long): ResponseEntity<Boolean> {
        tagService.deleteTag(id)
        return ResponseEntity.ok(true)
    }
}