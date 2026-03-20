package com.insurtech.backend.repository;

import com.insurtech.backend.entity.ClaimFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClaimFileRepository extends JpaRepository<ClaimFileEntity, UUID> {
}
