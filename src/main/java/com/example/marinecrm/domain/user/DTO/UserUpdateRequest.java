package com.example.marinecrm.domain.user.DTO;

import java.util.UUID;

public record UserUpdateRequest(UUID id, UserRequest payload) {
}
