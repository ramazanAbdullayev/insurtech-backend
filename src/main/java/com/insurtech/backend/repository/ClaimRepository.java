package com.insurtech.backend.repository;

import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.dto.api.response.ClaimResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    Optional<List<Claim>> findAllByUserId(UUID userId);

    Optional<Claim> findByClaimNumber(String claimNumber);
}
