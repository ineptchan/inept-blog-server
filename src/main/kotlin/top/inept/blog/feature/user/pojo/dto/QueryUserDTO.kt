package top.inept.blog.feature.user.pojo.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive
import org.hibernate.validator.constraints.Range
import top.inept.blog.base.BaseQueryDTO

data class QueryUserDTO (
    @Schema(description = "openapi.user.query_keyword")
    val keyword: String?,
) : BaseQueryDTO()