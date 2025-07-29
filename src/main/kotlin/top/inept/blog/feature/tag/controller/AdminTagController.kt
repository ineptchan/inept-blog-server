package top.inept.blog.feature.tag.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.ApiResponse
import top.inept.blog.feature.tag.pojo.convert.toTagVO
import top.inept.blog.feature.tag.pojo.dto.CreateTagDTO
import top.inept.blog.feature.tag.pojo.dto.UpdateTagDTO
import top.inept.blog.feature.tag.pojo.vo.TagVO
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
    fun getTags(): ApiResponse<List<TagVO>> {
        return ApiResponse.success(tagService.getTags().map { it.toTagVO() })
    }

    @Operation(summary = "根据id获取标签")
    @GetMapping("/{id}")
    fun getTagById(@PathVariable id: Long): ApiResponse<TagVO> {
        return ApiResponse.success(tagService.getTagById(id).toTagVO())
    }

    @Operation(summary = "创建标签")
    @PostMapping
    fun createTag(@Valid @RequestBody createTagDTO: CreateTagDTO): ApiResponse<TagVO> {
        return ApiResponse.success(tagService.createTag(createTagDTO).toTagVO())
    }

    @Operation(summary = "更新标签")
    @PutMapping
    fun updateTag(@Valid @RequestBody updateTagDTO: UpdateTagDTO): ApiResponse<TagVO> {
        return ApiResponse.success(tagService.updateTag(updateTagDTO).toTagVO())
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    fun deleteTag(@PathVariable id: Long): ApiResponse<Boolean> {
        tagService.deleteTag(id)
        return ApiResponse.success(true)
    }
}