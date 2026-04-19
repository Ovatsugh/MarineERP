package com.example.marinecrm.domain.user.DTO;

import com.example.marinecrm.enums.Roles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserUpdateRequest(
        UUID id,
        @NotBlank(message = "Nome é obrigatório") String name,
        @NotBlank(message = "Email é obrigatório") String email,
        String password,
        @NotNull(message = "Role é obrigatória") Roles role
) { }
