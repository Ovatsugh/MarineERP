package com.example.marinecrm.domain.company.DTO;

import jakarta.validation.constraints.NotBlank;

public record CompanyRequest(
        @NotBlank(message = "Nome é obrigatório") String name,
        @NotBlank(message = "CNPJ é obrigatório") String cnpj
) {
}
