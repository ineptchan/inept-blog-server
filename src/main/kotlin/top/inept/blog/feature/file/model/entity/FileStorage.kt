package top.inept.blog.feature.file.model.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import top.inept.blog.feature.file.model.entity.constraints.FileStorageConstraints
import top.inept.blog.feature.file.model.entity.enums.FileStorageStatus
import java.time.Instant

@Entity
@Table(
    name = "file_storage",
    uniqueConstraints = [
        UniqueConstraint(name = FileStorageConstraints.UNIQUE_OBJECT_NAME, columnNames = ["object_name"]),
        UniqueConstraint(name = FileStorageConstraints.UNIQUE_SHA_256, columnNames = ["sha_256"]),
    ]
)
class FileStorage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "object_name", nullable = false)
    var objectName: String,

    @Column(name = "original_file_name", nullable = false)
    var originalFileName: String,

    @Column(name = "mime_type")
    var mimeType: String,

    @Column(name = "size_bytes", nullable = false)
    var sizeBytes: Long,

    @Column(name = "sha_256", nullable = false)
    var sha256: String,

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    var status: FileStorageStatus = FileStorageStatus.PENDING,

    @Column(name = "bucket", nullable = false)
    var bucket: String,

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: Instant = Instant.now(),

    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null,

    //ip?...
)