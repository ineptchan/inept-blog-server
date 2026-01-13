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
import top.inept.blog.feature.categories.model.convert.toCategoriesVO
import top.inept.blog.feature.categories.model.dto.QueryCategoriesDTO
import top.inept.blog.feature.categories.model.vo.CategoriesVO
import top.inept.blog.feature.categories.service.CategoriesService

@Tag(name = "公开分类接口")
@RestController
@RequestMapping("/public/categories")
@Validated
class OpenCategoriesController(
    val categoriesService: CategoriesService
) {
    @Operation(summary = "获取分类列表")
    @GetMapping
    fun getCategories(@Valid queryCategoriesDTO: QueryCategoriesDTO): ApiResponse<PageResponse<CategoriesVO>> {
        return categoriesService
            .getCategories(queryCategoriesDTO)
            .toPageResponse { it.toCategoriesVO() }
            .toApiResponse()
    }
}