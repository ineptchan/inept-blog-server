package top.inept.blog.feature.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import top.inept.blog.feature.user.model.entity.Permission

@Repository
interface PermissionRepository : JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

}