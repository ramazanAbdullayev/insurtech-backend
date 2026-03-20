package com.insurtech.backend.repository;

import com.insurtech.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByEmail(String email);

    @Query("""
            SELECT UserEntity.passwordHash FROM UserEntity WHERE UserEntity.email = email
        """)
    String findByEmail(String email);
}
