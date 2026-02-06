package top.inept.blog.feature.categories.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import top.inept.blog.base.PageResponse
import top.inept.blog.extensions.toPageResponse
import top.inept.blog.feature.categories.model.convert.toCategoriesVO
import top.inept.blog.feature.categories.model.dto.CreateCategoriesDTO
import top.inept.blog.feature.categories.model.dto.QueryCategoriesDTO
import top.inept.blog.feature.categories.model.dto.UpdateCategoriesDTO
import top.inept.blog.feature.categories.model.vo.CategoriesVO
import top.inept.blog.feature.categories.service.CategoriesService

@Tag(name = "分类接口")
@SecurityRequirement(name = "accessToken")
@RestController
@RequestMapping("/admin/categories")
@Validated
class AdminCategoriesController(
    private val categoriesService: CategoriesService
) {
    @PreAuthorize("hasAuthority('admin:categories:read')")
    @Operation(summary = "获取分类列表")
    @GetMapping
    fun getCategories(@Valid queryCategoriesDTO: QueryCategoriesDTO): ResponseEntity<PageResponse<CategoriesVO>> {
        return ResponseEntity.ok(
            categoriesService
                .getCategories(queryCategoriesDTO)
                .toPageResponse { it.toCategoriesVO() }
        )
    }

    @PreAuthorize("hasAuthority('admin:categories:read')")
    @Operation(summary = "根据id获取分类")
    @GetMapping("/{id}")
    fun getCategoriesById(@PathVariable id: Long): ResponseEntity<CategoriesVO> {
        return ResponseEntity.ok(categoriesService.getCategoriesById(id).toCategoriesVO())
    }

    @PreAuthorize("hasAuthority('admin:categories:write')")
    @Operation(summary = "创建分类")
    @PostMapping
    fun createCategory(@Valid @RequestBody createCategoriesDTO: CreateCategoriesDTO): ResponseEntity<CategoriesVO> {
        return ResponseEntity.ok(categoriesService.createCategory(createCategoriesDTO).toCategoriesVO())
    }

    @PreAuthorize("hasAuthority('admin:categories:modify')")
    @Operation(summary = "更新分类")
    @PatchMapping("/{id}")
    fun updateCategory(
        @Valid @RequestBody updateCategoriesDTO: UpdateCategoriesDTO,
        @PathVariable id: Long
    ): ResponseEntity<CategoriesVO> {
        return ResponseEntity.ok(categoriesService.updateCategory(id, updateCategoriesDTO).toCategoriesVO())
    }

    @PreAuthorize("hasAuthority('admin:categories:delete')")
    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<Boolean> {
        categoriesService.deleteCategory(id)
        return ResponseEntity.ok(true)
    }
}