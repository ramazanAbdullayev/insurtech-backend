package com.insurtech.backend.repository;

import com.insurtech.backend.domain.entity.ClaimFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClaimFileRepository extends JpaRepository<ClaimFile, UUID> {
}
