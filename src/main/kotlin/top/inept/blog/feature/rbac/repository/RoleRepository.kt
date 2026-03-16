package top.inept.blog.feature.rbac.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository
import top.inept.blog.feature.rbac.model.entity.Role

@Repository
interface RoleRepository : JpaRepository<Role, Long>, JpaSpecificationExecutor<Role>,
    QuerydslPredicateExecutor<Role> {
    @Modifying
    fun deleteRoleById(id: Long): Int

    @EntityGraph(attributePaths = ["permissionBindings", "permissionBindings.permission"])
    fun findWithPermissionsById(id: Long): Role?
}