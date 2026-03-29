package com.example.marinecrm.domain.product.DTO;

import com.example.marinecrm.domain.product.Product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(UUID id, String name, BigDecimal price, Integer stock_quantity, String bikeModel,
                              String code, String description) {

    public ProductResponse(Product product) {
        this(product.getId(), product.getName(), product.getPrice(), product.getStock_quantity(), product.getBikeModel(),
                product.getCode(), product.getDescription());
    }
}
