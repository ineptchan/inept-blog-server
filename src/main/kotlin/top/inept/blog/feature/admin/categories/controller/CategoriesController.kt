package top.inept.blog.feature.admin.categories.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.ApiResponse
import top.inept.blog.feature.admin.categories.pojo.convert.toCategories
import top.inept.blog.feature.admin.categories.pojo.convert.toCategoriesVO
import top.inept.blog.feature.admin.categories.pojo.dto.CategoriesDTO
import top.inept.blog.feature.admin.categories.pojo.vo.CategoriesVO
import top.inept.blog.feature.admin.categories.service.CategoriesService

@Tag(name = "分类管理")
@RestController
@RequestMapping("/admin/categories")
@Validated
class CategoriesController(
    private val categoriesService: CategoriesService
) {
    @Operation(summary = "获取分类列表")
    @GetMapping
    fun getCategories(): ApiResponse<List<CategoriesVO>> {
        return ApiResponse.success(categoriesService.getCategories().map { it.toCategoriesVO() })
    }

    @Operation(summary = "根据id获取分类")
    @GetMapping("/{id}")
    fun getCategoriesById(@PathVariable id: Long): ApiResponse<CategoriesVO> {
        return ApiResponse.success(categoriesService.getCategoriesById(id).toCategoriesVO())
    }

    @Operation(summary = "创建分类")
    @PostMapping
    fun createCategory(@RequestBody categories: CategoriesDTO): ApiResponse<CategoriesVO> {
        return ApiResponse.success(categoriesService.createCategory(categories.toCategories()).toCategoriesVO())
    }

    @Operation(summary = "更新分类")
    @PutMapping
    fun updateCategory(@RequestBody categories: CategoriesDTO): ApiResponse<CategoriesVO> {
        return ApiResponse.success(categoriesService.updateCategory(categories.toCategories()).toCategoriesVO())
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ApiResponse<Boolean> {
        categoriesService.deleteCategory(id)
        return ApiResponse.success(true)
    }
}