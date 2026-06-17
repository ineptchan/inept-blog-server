package top.inept.blog.feature.objectstorage.model.entity.enums

import top.inept.blog.feature.objectstorage.model.entity.enums.Purpose.*
import top.inept.blog.properties.ObjectStorageProperties

enum class Purpose {
    /**
     * 头像
     */
    AVATAR,

    /**
     * 文章正文图片
     */
    ARTICLE_IMAGE,

    /**
     * 文章封面图
     */
    ARTICLE_FEATURED_IMAGE,

    /**
     * 文章视频
     */
    ARTICLE_VIDEO,

    /**
     * 文章附件
     */
    ARTICLE_ATTACHMENT,
}

fun Purpose.getBucketName(): String = when (this) {
    AVATAR -> "blog-avatar"
    ARTICLE_IMAGE -> "blog-article-image"
    ARTICLE_FEATURED_IMAGE -> "blog-article-featured-image"
    ARTICLE_VIDEO -> "blog-article-video"
    ARTICLE_ATTACHMENT -> "blog-article-attachment"
}

fun Purpose.getPendingBucketName(): String = "${this.getBucketName()}-pending"

fun Purpose.getMaxSize(osp: ObjectStorageProperties): Long = when (this) {
    AVATAR -> osp.avatar.maxFileSize
    ARTICLE_IMAGE -> osp.articleImage.maxFileSize
    ARTICLE_FEATURED_IMAGE -> osp.articleFeaturedImage.maxFileSize
    ARTICLE_VIDEO -> osp.articleVideo.maxFileSize
    ARTICLE_ATTACHMENT -> osp.articleAttachment.maxFileSize
}