package top.inept.blog.base

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.PositiveOrZero
import org.hibernate.validator.constraints.Range

open class BaseQueryDTO(
    @field:Schema(description = "openapi.query.page")
    @field:PositiveOrZero(message = "valid.common.query.page")
    open var page: Int = 0,

    @field:Schema(description = "openapi.query.size")
    @field:Range(min = 1, max = 200, message = "valid.common.query.size")
    open var size: Int = 30
)