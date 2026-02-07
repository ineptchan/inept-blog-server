package top.inept.blog.feature.user.model.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.base.BaseQueryDTO

data class QueryUserDTO (
    @field:Schema(description = "openapi.user.query_keyword")
    val keyword: String?,
) : BaseQueryDTO()