package com.insurtech.backend.repository;

import com.insurtech.backend.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // Revoke all refresh tokens with the specified familyId.
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE RefreshToken rt
        SET rt.status = com.insurtech.backend.domain.enums.TokenStatus.REVOKED
        WHERE rt.familyId = :familyId
        """)
    void revokeFamily(@Param("familyId") UUID familyId);

    // Logout-all-devices (revoke all refresh tokens with the specified userId)
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE RefreshToken rt SET rt.status = com.insurtech.backend.domain.enums.TokenStatus.REVOKED
        WHERE rt.user.id = :userId
            AND rt.status = com.insurtech.backend.domain.enums.TokenStatus.ACTIVE
        """)
    int revokeAllActiveForUser(@Param("userId") UUID userId);

    // Delete expired tokens older than a threshold
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :cutoff")
    int deleteExpiredBefore(@Param("cutoff") Instant cutoff);
}
