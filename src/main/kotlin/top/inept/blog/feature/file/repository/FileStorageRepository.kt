package top.inept.blog.feature.file.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import top.inept.blog.feature.file.model.entity.FileStorage

@Repository
interface FileStorageRepository : JpaRepository<FileStorage, Long>, JpaSpecificationExecutor<FileStorage> {
    fun findFileStorageByOriginalFileName(objectName: String): FileStorage?

}