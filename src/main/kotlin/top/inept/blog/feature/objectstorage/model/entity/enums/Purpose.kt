package top.inept.blog.feature.objectstorage.model.entity.enums

enum class Purpose(
    val objectKey: String,
) {
    /**
     * 头像
     */
    AVATAR("avatar"),

    /**
     * 文章图片
     */
    ARTICLE_IMAGE("article_image"),

    /**
     * 文章图片
     */
    ARTICLE_FEATURED_IMAGE("article_featured_image"),

    /**
     * 文章视频
     */
    ARTICLE_VIDEO("article_video"),

    /**
     * 文章附件
     */
    ARTICLE_ATTACHMENT("article_attachment"),
}