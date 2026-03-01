package top.inept.blog.base

data class ValidationError(
    val field: String,
    val code: String,
    val message: String,
)
