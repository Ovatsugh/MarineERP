package com.example.marinecrm.domain.user.DTO;

import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.enums.Roles;

import java.util.UUID;

public record UserResponse(UUID id, String name, String email, Roles role) {

    public UserResponse(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
