package com.insurtech.backend.repository;

import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.domain.entity.ClaimFile;
import com.insurtech.backend.dto.api.response.ClaimFileResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimFileRepository extends JpaRepository<ClaimFile, UUID> {
  List<ClaimFile> findAllByClaim(Claim claim);

  List<ClaimFileResponse> findAllByClaimId(UUID claimId);
}
