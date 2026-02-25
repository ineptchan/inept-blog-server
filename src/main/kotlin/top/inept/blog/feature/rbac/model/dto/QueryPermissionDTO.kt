package top.inept.blog.feature.rbac.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.base.BaseQueryDTO

data class QueryPermissionDTO(
    @field:Schema(description = "openapi.permission.query_keyword")
    val keyword: String?,
) : BaseQueryDTO()