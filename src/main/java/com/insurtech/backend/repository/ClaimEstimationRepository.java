package com.insurtech.backend.repository;

import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.domain.entity.ClaimEstimation;
import com.insurtech.backend.domain.enums.ClaimEstimationStatus;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClaimEstimationRepository extends JpaRepository<ClaimEstimation, UUID> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT ce FROM ClaimEstimation ce WHERE ce.id = :id")
  Optional<ClaimEstimation> lockById(@Param("id") UUID id);

  @Query(
      """
      SELECT ce FROM ClaimEstimation ce
      WHERE ce.status = :status
      AND ce.attemptCount <= :maxAttempt
      """)
  List<ClaimEstimation> findRetryable(
      @Param("status") ClaimEstimationStatus status,
      @Param("maxAttempt") int maxAttempt,
      Pageable pageable);

  @Query(
      """
        SELECT ce
        FROM ClaimEstimation ce
        WHERE ce.status IN :statuses
        AND ce.updatedAt < :threshold
      """)
  List<ClaimEstimation> findStuckEstimations(
      @Param("statuses") List<ClaimEstimationStatus> statuses,
      @Param("threshold") Instant threshold,
      Pageable pageable);

  @Modifying
  @Query(
      value =
          """
            INSERT INTO claim_estimation (id, claim_id, status, attempt_count, created_at, updated_at)
            VALUES (gen_random_uuid(), :claimId, 'PENDING', 0, now(), now())
            ON CONFLICT (claim_id) DO NOTHING
            """,
      nativeQuery = true)
  void insertIfAbsent(@Param("claimId") UUID claimId);

  Optional<ClaimEstimation> findByClaim_Id(UUID claimId);

  void deleteByClaim(Claim claim);
}
