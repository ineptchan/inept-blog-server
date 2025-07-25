package top.inept.blog.feature.admin.categories.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.ApiResponse
import top.inept.blog.feature.admin.categories.pojo.convert.toCategoriesVO
import top.inept.blog.feature.admin.categories.pojo.dto.UpdateCategoriesDTO
import top.inept.blog.feature.admin.categories.pojo.dto.CreateCategoriesDTO
import top.inept.blog.feature.admin.categories.pojo.vo.CategoriesVO
import top.inept.blog.feature.admin.categories.service.CategoriesService

@Tag(name = "管理员分类接口")
@RestController("adminCategoriesController")
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
    fun createCategory(@Valid @RequestBody createCategoriesDTO: CreateCategoriesDTO): ApiResponse<CategoriesVO> {
        return ApiResponse.success(categoriesService.createCategory(createCategoriesDTO).toCategoriesVO())
    }

    @Operation(summary = "更新分类")
    @PutMapping
    fun updateCategory(@Valid @RequestBody updateCategoriesDTO: UpdateCategoriesDTO): ApiResponse<CategoriesVO> {
        return ApiResponse.success(categoriesService.updateCategory(updateCategoriesDTO).toCategoriesVO())
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ApiResponse<Boolean> {
        categoriesService.deleteCategory(id)
        return ApiResponse.success(true)
    }
}