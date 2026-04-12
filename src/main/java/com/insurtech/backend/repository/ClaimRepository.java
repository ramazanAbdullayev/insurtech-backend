package com.insurtech.backend.repository;

import com.insurtech.backend.domain.entity.Claim;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID> {
  Optional<List<Claim>> findAllByUserId(UUID userId);

  Optional<Claim> findByClaimNumber(String claimNumber);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("DELETE FROM Claim c WHERE c.id = :id")
  void deleteByIdBulk(@Param("id") UUID id);
}
