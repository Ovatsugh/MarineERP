package com.example.marinecrm.domain.product.DTO;

import java.util.UUID;

public record ProductUpdateRequest(UUID id, ProductRequest payload) {
}
