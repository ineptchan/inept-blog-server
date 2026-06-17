package top.inept.blog.feature.objectstorage.model.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import top.inept.blog.feature.objectstorage.model.entity.constraints.ObjectStorageConstraints
import top.inept.blog.feature.objectstorage.model.entity.enums.Purpose
import top.inept.blog.feature.objectstorage.model.entity.enums.Status
import top.inept.blog.feature.objectstorage.model.entity.enums.Visibility
import top.inept.blog.feature.user.model.entity.User
import java.time.Instant

@Entity
@Table(
    name = "object_storage_table",
    uniqueConstraints = [
        //  UniqueConstraint(name = ObjectStorageConstraints.UNIQUE_SHA_256, columnNames = ["sha256"]),
        UniqueConstraint(name = ObjectStorageConstraints.UNIQUE_OBJECT_KEY, columnNames = ["object_key"]),
    ]
)
class ObjectStorage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    var ownerUser: User,

    /**
     * 对象的key
     */
    @Column(name = "object_key", nullable = false)
    var objectKey: String,

    /**
     * 使用目的
     */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "purpose", nullable = false, columnDefinition = "object_storage_purpose")
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
    @Column(name = "file_size", nullable = false)
    var fileSize: Long,

    /**
     * 文件hash
     */
    @Column(name = "file_hash", length = 64)
    var fileHash: String? = null,

    /**
     * 桶
     */
    @Column(name = "bucket", nullable = false)
    var bucket: String,

    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    var status: Status,

    /**
     * 保留字段业务层语义
     */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "visibility", nullable = false)
    var visibility: Visibility,

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)