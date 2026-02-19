package top.inept.blog.exception

data class SystemException(
    val msg: String,
) : RuntimeException(msg)