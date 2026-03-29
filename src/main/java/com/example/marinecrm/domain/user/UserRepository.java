package com.example.marinecrm.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<User> findByCompany_Id(UUID companyId, Pageable pageable);
    Optional<User> findByIdAndCompany_Id(UUID id, UUID companyId);
}
