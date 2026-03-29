package com.example.marinecrm.domain.company.DTO;

import com.example.marinecrm.domain.company.Company;

import java.util.UUID;

public record CompanyResponse(UUID id, String name, String cnpj) {
    public CompanyResponse(Company company) {
        this(company.getId(), company.getName(), company.getCnpj());
    }
}
