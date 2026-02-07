package top.inept.blog.feature.file.model.entity.enums

enum class FileStorageStatus {
    /**
     * 待处理
     */
    PENDING,

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