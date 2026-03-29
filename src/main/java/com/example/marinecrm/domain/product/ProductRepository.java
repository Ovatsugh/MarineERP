package com.example.marinecrm.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findByUser_Company_Id(UUID companyId, Pageable pageable);
    Optional<Product> findByIdAndUser_Company_Id(UUID id, UUID companyId);
}
