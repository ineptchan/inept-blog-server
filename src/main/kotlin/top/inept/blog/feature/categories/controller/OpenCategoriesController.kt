package top.inept.blog.feature.categories.controller

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
import top.inept.blog.feature.categories.pojo.convert.toCategoriesVO
import top.inept.blog.feature.categories.pojo.dto.CategoriesQueryDTO
import top.inept.blog.feature.categories.pojo.vo.CategoriesVO
import top.inept.blog.feature.categories.service.CategoriesService

@Tag(name = "公开分类接口")
@RestController
@RequestMapping("/open/categories")
@Validated
class OpenCategoriesController(
    val categoriesService: CategoriesService
) {
    @Operation(summary = "获取分类列表")
    @GetMapping
    fun getCategories(@Valid categoriesQueryDTO: CategoriesQueryDTO): ApiResponse<PageResponse<CategoriesVO>> {
        return categoriesService
            .getCategories(categoriesQueryDTO)
            .toPageResponse { it.toCategoriesVO() }
            .toApiResponse()
    }
}