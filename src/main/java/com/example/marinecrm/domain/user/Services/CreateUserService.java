package com.example.marinecrm.domain.user.Services;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.company.CompanyRepository;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.UserRepository;
import com.example.marinecrm.domain.user.DTO.UserRequest;
import com.example.marinecrm.domain.user.DTO.UserResponse;
import com.example.marinecrm.enums.Roles;
import com.example.marinecrm.exceptions.InvalidRoleException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateUserService implements Command<UserRequest, UserResponse> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse execute(UserRequest request) {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        switch (request.role()) {
            case ADMIN, EMPLOYEE -> {}
            default -> throw new InvalidRoleException("Role inválida: apenas ADMIN e EMPLOYEE são permitidos");
        }

        User user = new User(request, passwordEncoder.encode(request.password()), authenticatedUser.getCompany());

        User saved = userRepository.save(user);

        return new UserResponse(saved);
    }
}
