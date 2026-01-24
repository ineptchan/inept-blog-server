package top.inept.blog.feature.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository
import top.inept.blog.feature.user.model.entity.User

@Repository
interface UserRepository : JpaRepository<User, Long>, JpaSpecificationExecutor<User>,
    QuerydslPredicateExecutor<User>{
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): User?
    fun existsByEmail(email: String): Boolean
    fun existsByNickname(nickname: String): Boolean

    @Query(
        """
select distinct p.code
from UserRole ur
join ur.role r
join RolePermission rp on rp.role = r
join rp.permission p
where ur.user.id = :userId
    """
    )
    fun findPermissionCodes(userId: Long): List<String>
}