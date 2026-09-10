package top.inept.blog.feature.objectstorage.model.entity.enums

import top.inept.blog.properties.ObjectStorageProperties

enum class Purpose(
    val bucketName: String,
    val permissionCode: String? = null,
) {
    /**
     * 头像
     */
    AVATAR(
        bucketName = "blog-avatar",
    ),

    /**
     * 文章正文图片
     */
    ARTICLE_IMAGE(
        bucketName = "blog-article-image",
        permissionCode = "admin:article:upload:image",
    ),

    /**
     * 文章封面图
     */
    ARTICLE_FEATURED_IMAGE(
        bucketName = "blog-article-featured-image",
        permissionCode = "admin:article:upload:featured-image",
    ),

    /**
     * 文章视频
     */
    ARTICLE_VIDEO(
        bucketName = "blog-article-video",
        permissionCode = "admin:article:upload:video",
    ),

    /**
     * 文章附件
     */
    ARTICLE_ATTACHMENT(
        bucketName = "blog-article-attachment",
        permissionCode = "admin:article:upload:attachment",
    );

    val pendingBucketName: String get() = "$bucketName-pending"

    fun getMaxSize(properties: ObjectStorageProperties): Long =
        when (this) {
            AVATAR -> properties.avatar.maxFileSize
            ARTICLE_IMAGE -> properties.articleImage.maxFileSize
            ARTICLE_FEATURED_IMAGE -> properties.articleFeaturedImage.maxFileSize
            ARTICLE_VIDEO -> properties.articleVideo.maxFileSize
            ARTICLE_ATTACHMENT -> properties.articleAttachment.maxFileSize
        }
}