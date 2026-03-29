package com.example.marinecrm.domain.company.DTO;

import java.util.UUID;

public record CompanyUpdateRequest(UUID id, CompanyRequest payload) {
}
