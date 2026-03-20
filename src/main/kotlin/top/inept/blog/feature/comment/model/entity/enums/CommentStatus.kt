package top.inept.blog.feature.comment.model.entity.enums

enum class CommentStatus {
    /**
     * 待审核
     */
    PENDING,

    /**
     * 已发布
     */
    PUBLISHED,

    /**
     * 已删除
     */
    DELETED,

    /**
     * 垃圾评论
     */
    SPAM
}