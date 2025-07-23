package top.inept.blog.feature.admin.tag.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.ApiResponse
import top.inept.blog.feature.admin.tag.pojo.convert.toTag
import top.inept.blog.feature.admin.tag.pojo.convert.toTagVO
import top.inept.blog.feature.admin.tag.pojo.dto.CreateTagDTO
import top.inept.blog.feature.admin.tag.pojo.dto.TagDTO
import top.inept.blog.feature.admin.tag.pojo.vo.TagVO
import top.inept.blog.feature.admin.tag.service.TagService

@Tag(name = "管理员标签接口")
@RestController("adminTagController")
@RequestMapping("/admin/tag")
@Validated
class TagController(
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
    fun createTag(@RequestBody tag: CreateTagDTO): ApiResponse<TagVO> {
        return ApiResponse.success(tagService.createTag(tag.toTag()).toTagVO())
    }

    @Operation(summary = "更新标签")
    @PutMapping
    fun updateTag(@RequestBody tag: TagDTO): ApiResponse<TagVO> {
        return ApiResponse.success(tagService.updateTag(tag.toTag()).toTagVO())
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    fun deleteTag(@PathVariable id: Long): ApiResponse<Boolean> {
        tagService.deleteTag(id)
        return ApiResponse.success(true)
    }
}