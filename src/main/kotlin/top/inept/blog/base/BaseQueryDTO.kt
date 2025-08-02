package top.inept.blog.base

import jakarta.validation.constraints.Positive
import org.hibernate.validator.constraints.Range

open class BaseQueryDTO(
    @field:Positive
    val page: Int = 1,

    @field:Range(min = 1, max = 100)
    val size: Int = 30
)