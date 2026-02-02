package top.inept.blog.feature.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import top.inept.blog.feature.user.model.entity.Role

@Repository
interface RoleRepository : JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

}