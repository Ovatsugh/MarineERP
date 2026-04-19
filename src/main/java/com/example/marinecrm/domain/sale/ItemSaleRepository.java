package com.example.marinecrm.domain.sale;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemSaleRepository extends JpaRepository<ItemSale, UUID> {
    List<ItemSale> findByProduct_Id(UUID productId);
}
