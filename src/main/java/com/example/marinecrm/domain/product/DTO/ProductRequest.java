package com.example.marinecrm.domain.product.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Nome é obrigatório") String name,
        @NotNull(message = "Preço é obrigatório") BigDecimal price,
        @NotNull(message = "Quantidade é obrigatória") Integer stock_quantity,
        @NotBlank(message = "Modelo da moto é obrigatório") String bikeModel,
        String description,
        String code
) {
}
