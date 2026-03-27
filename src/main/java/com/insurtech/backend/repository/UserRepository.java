package com.insurtech.backend.repository;

import com.insurtech.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    @Query("""
            SELECT User.passwordHash FROM User WHERE User.email = email
        """)
    String findByEmail(String email);
}
