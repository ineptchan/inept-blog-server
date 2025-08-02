package top.inept.blog.feature.tag.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import top.inept.blog.base.BaseQueryDTO

data class QueryTagDTO(
    @Schema(description = "openapi.tag.query_keyword")
    val keyword: String?,
) : BaseQueryDTO()