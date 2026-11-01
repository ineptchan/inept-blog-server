package top.inept.blog.feature.user.model.projection

interface UserAuthStateProjection {
    val status: Boolean
    val authVersion: Long
}