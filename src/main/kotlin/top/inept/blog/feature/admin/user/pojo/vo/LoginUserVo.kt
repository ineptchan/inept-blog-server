package top.inept.blog.feature.admin.user.pojo.vo

data class LoginUserVo(
    val id: Long = 0,
    val username: String,
    val token: String,
)