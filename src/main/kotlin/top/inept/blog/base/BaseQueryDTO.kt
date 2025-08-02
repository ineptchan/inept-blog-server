package top.inept.blog.base

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive
import org.hibernate.validator.constraints.Range

open class BaseQueryDTO(
    @Schema(description = "openapi.query.page")
    @field:Positive(message = "valid.common.query.page")
    val page: Int = 1,

    @Schema(description = "openapi.query.size")
    @field:Range(min = 1, max = 100, message = "valid.common.query.size")
    val size: Int = 30
)