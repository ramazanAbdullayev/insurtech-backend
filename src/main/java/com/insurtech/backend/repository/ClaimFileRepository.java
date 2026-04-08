package com.insurtech.backend.repository;

import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.domain.entity.ClaimFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClaimFileRepository extends JpaRepository<ClaimFile, UUID> {
    List<ClaimFile> findAllByClaim(Claim claim);
}
