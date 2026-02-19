package top.inept.blog.feature.objectstorage.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import top.inept.blog.feature.objectstorage.model.entity.constraints.ObjectStorageConstraints
import top.inept.blog.feature.objectstorage.model.entity.enums.ObjectStorageStatus
import top.inept.blog.feature.objectstorage.model.entity.enums.Purpose
import top.inept.blog.feature.objectstorage.model.entity.enums.Visibility
import java.time.Instant

@Entity
@Table(
    name = "object_storage",
    uniqueConstraints = [
        UniqueConstraint(name = ObjectStorageConstraints.UNIQUE_SHA_256, columnNames = ["sha256"]),
        UniqueConstraint(name = ObjectStorageConstraints.UNIQUE_OBJECT_KEY, columnNames = ["object_key"]),
    ]
)
//TODO 考虑可重复的sha256
class ObjectStorage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    /**
     * 归属用户
     */
    @Column(name = "owner_user_id", nullable = false)
    var ownerUserId: Long,

    /**
     * 使用目的
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false)
    var purpose: Purpose,

    /**
     * 原始文件名
     */
    @Column(name = "original_filename", nullable = false)
    var originalFileName: String,

    /**
     * MIME
     */
    @Column(name = "content_type", nullable = false)
    var contentType: String,

    /**
     * 对象大小
     */
    @Column(name = "size_bytes", nullable = false)
    var sizeBytes: Long,

    /**
     * 用于 秒传/去重/校验
     */
    @Column(name = "sha256", nullable = false)
    var sha256: String,

    /**
     * 桶
     */
    @Column(name = "bucket", nullable = false)
    var bucket: String,

    /**
     * 对象的key
     */
    @Column(name = "object_key")
    var objectKey: String,

    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ObjectStorageStatus,

    /**
     * 保留字段业务层语义
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    var visibility: Visibility,

    /**
     * 创建时间,这里值的是数据库实体的
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),

    /**
     * 更新时间,这里值的是数据库实体的
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)