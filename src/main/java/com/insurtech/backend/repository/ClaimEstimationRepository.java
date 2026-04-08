package com.insurtech.backend.repository;

import com.insurtech.backend.domain.entity.ClaimEstimation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimEstimationRepository extends JpaRepository<ClaimEstimation, UUID> {}
