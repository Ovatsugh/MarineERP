package com.example.marinecrm.domain.user.Services;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.UserRepository;
import com.example.marinecrm.domain.user.DTO.UserResponse;
import com.example.marinecrm.domain.user.DTO.UserUpdateRequest;
import com.example.marinecrm.exceptions.ForbiddenException;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserService implements Command<UserUpdateRequest, UserResponse> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse execute(UserUpdateRequest request) {
        User user = userRepository.findById(request.id()).orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado: " + request.id()));

        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getCompany().getId().equals(authenticatedUser.getCompany().getId())) {
            throw new ForbiddenException();
        }

        user.update(request, passwordEncoder.encode(request.payload().password()));

        User saved = userRepository.save(user);

        return new UserResponse(saved);
    }
}
