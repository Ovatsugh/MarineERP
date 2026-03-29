package com.example.marinecrm.domain.user.DTO;

import com.example.marinecrm.enums.Roles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserRequest(
        @NotBlank(message = "Nome é obrigatório") String name,
        @NotBlank(message = "Email é obrigatório") String email,
        @NotBlank(message = "Senha é obrigatória") String password,
        @NotNull(message = "Role é obrigatória") Roles role

) { }
