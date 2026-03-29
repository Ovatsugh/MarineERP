package com.example.marinecrm.domain.user.Services;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.UserRepository;
import com.example.marinecrm.domain.user.DTO.UserResponse;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserByIdService implements Command<UUID, UserResponse> {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse execute(UUID id) {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findByIdAndCompany_Id(id, authenticatedUser.getCompany().getId())
                .map(UserResponse::new)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }
}
