package top.inept.blog.feature.auth.repository

import org.springframework.data.jpa.repository.*
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import top.inept.blog.feature.auth.model.entity.RefreshToken
import java.time.Instant
import java.util.*

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long>, JpaSpecificationExecutor<RefreshToken> {
    @EntityGraph(attributePaths = ["user"])
    fun findByTokenHash(refreshTokenSha256: String): RefreshToken?

    @Modifying
    @Query(
        """
    update RefreshToken rt
    set rt.revokedAt = :now
    where rt.user.id = :userId
      and rt.expiresAt > :now
      and rt.revokedAt is null
"""
    )
    fun revokeActiveTokenByUserId(
        @Param("userId") userId: Long,
        @Param("now") now: Instant
    ): Int

    @Modifying
    @Query("update RefreshToken rt set rt.revokedAt = :now where rt.familyId = :familyId")
    fun revokeTokenByFamilyId(
        @Param("familyId") familyId: UUID,
        @Param("now") now: Instant
    ): Int
}