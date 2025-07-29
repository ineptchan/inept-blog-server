package top.inept.blog.feature.tag.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.base.ApiResponse
import top.inept.blog.feature.tag.pojo.convert.toTagVO
import top.inept.blog.feature.tag.pojo.vo.TagVO
import top.inept.blog.feature.tag.service.TagService

@Tag(name = "公开标签接口")
@RestController
@RequestMapping("/open/tag")
@Validated
class OpenTagController(
    private val tagService: TagService,
) {
    @Operation(summary = "获取标签列表")
    @GetMapping()
    fun getTags(): ApiResponse<List<TagVO>> {
        return ApiResponse.success(tagService.getTags().map { it.toTagVO() })
    }
}