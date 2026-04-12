package com.insurtech.backend.service;

import com.insurtech.backend.domain.entity.Claim;
import java.util.UUID;

public interface ClaimEstimationService {

  void estimate(UUID claimId);

  void delete(Claim claim);
}
