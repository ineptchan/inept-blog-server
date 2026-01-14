package top.inept.blog.feature.auth.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import top.inept.blog.feature.auth.model.entity.RefreshToken

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long>, JpaSpecificationExecutor<RefreshToken> {
    @EntityGraph(attributePaths = ["user"])
    fun findByTokenHash(refreshTokenSha256: String): RefreshToken?


}