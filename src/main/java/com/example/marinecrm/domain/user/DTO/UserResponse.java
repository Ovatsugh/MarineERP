package com.example.marinecrm.domain.user.DTO;

import com.example.marinecrm.domain.user.User;

import java.util.UUID;

public record UserResponse(UUID id, String name, String email) {

    public UserResponse(User user) {
        this(user.getId(), user.getName(), user.getEmail());
    }
}
