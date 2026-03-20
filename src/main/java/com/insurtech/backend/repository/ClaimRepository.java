package com.insurtech.backend.repository;

import com.insurtech.backend.entity.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<ClaimEntity, UUID> {
}
