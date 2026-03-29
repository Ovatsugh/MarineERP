package com.example.marinecrm.domain.user.Services;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.UserRepository;
import com.example.marinecrm.exceptions.ForbiddenException;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteUserService implements Command<UUID, Void> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public Void execute(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado: " + id));

        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!user.getCompany().getId().equals(authenticatedUser.getCompany().getId())) {
            throw new ForbiddenException();
        }

        userRepository.delete(user);
        return null;
    }
}
