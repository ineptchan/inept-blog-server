package top.inept.blog.feature.tag.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.base.ApiResponse
import top.inept.blog.base.PageResponse
import top.inept.blog.extensions.toApiResponse
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.feature.tag.model.convert.toTagVO
import top.inept.blog.feature.tag.model.dto.QueryTagDTO
import top.inept.blog.feature.tag.model.vo.TagVO
import top.inept.blog.feature.tag.service.TagService

@Tag(name = "公开标签接口")
@RestController
@RequestMapping("/public/tag")
@Validated
class OpenTagController(
    private val tagService: TagService,
) {
    @Operation(summary = "获取标签列表")
    @GetMapping()
    fun getTags(@Valid queryTagDTO: QueryTagDTO): ApiResponse<PageResponse<TagVO>> {
        return tagService
            .getTags(queryTagDTO)
            .toPageResponse { it.toTagVO() }
            .toApiResponse()
    }
}