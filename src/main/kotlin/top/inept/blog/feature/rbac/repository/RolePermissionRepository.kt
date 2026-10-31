package top.inept.blog.feature.rbac.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import top.inept.blog.feature.rbac.model.entity.RolePermission
import top.inept.blog.feature.rbac.model.entity.RolePermissionId

@Repository
interface RolePermissionRepository : JpaRepository<RolePermission, RolePermissionId>