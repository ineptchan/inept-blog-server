package top.inept.blog.feature.tag.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.inept.blog.base.PageResponse
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.feature.tag.model.convert.toTagVO
import top.inept.blog.feature.tag.model.dto.QueryTagDTO
import top.inept.blog.feature.tag.model.vo.TagVO
import top.inept.blog.feature.tag.service.TagService

@Tag(name = "标签接口")
@RestController
@RequestMapping("/public/tag")
@Validated
class PublicTagController(
    private val tagService: TagService,
) {
    @Operation(summary = "获取标签列表")
    @GetMapping()
    fun getTags(@Valid dto: QueryTagDTO): ResponseEntity<PageResponse<TagVO>> {
        return ResponseEntity.ok(tagService.getTags(dto).toPageResponse { it.toTagVO() })
    }
}