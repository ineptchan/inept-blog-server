package top.inept.blog.feature.objectstorage.model.entity.enums

enum class ObjectStorageStatus {
    /**
     * 准备就绪
     */
    PREPARED,

    /**
     * 正在上传
     */
    UPLOADING,

    /**
     * 已上传
     */
    UPLOADED,

    /**
     * 失败
     */
    FAILED,

    /**
     * 已删除
     */
    DELETED
}