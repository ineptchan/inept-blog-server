package top.inept.blog.feature.categories.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.base.BaseQueryDTO

data class QueryCategoriesDTO(
    @field:Schema(description = "openapi.categories.query_keyword")
    val keyword: String?,
) : BaseQueryDTO()