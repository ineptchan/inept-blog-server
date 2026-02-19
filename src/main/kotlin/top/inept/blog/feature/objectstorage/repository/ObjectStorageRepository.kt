package top.inept.blog.feature.objectstorage.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository
import top.inept.blog.feature.objectstorage.model.entity.ObjectStorage

@Repository
interface ObjectStorageRepository : JpaRepository<ObjectStorage, Long>, JpaSpecificationExecutor<ObjectStorage>,
    QuerydslPredicateExecutor<ObjectStorage>