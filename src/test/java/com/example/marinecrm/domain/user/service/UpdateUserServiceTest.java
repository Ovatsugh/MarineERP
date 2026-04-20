package com.example.marinecrm.domain.user.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.UserRepository;
import com.example.marinecrm.domain.user.DTO.UserResponse;
import com.example.marinecrm.domain.user.DTO.UserUpdateRequest;
import com.example.marinecrm.domain.user.Services.UpdateUserService;
import com.example.marinecrm.enums.Roles;
import com.example.marinecrm.exceptions.ForbiddenException;
import com.example.marinecrm.exceptions.InvalidRoleException;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UpdateUserService updateUserService;

    private User authenticatedUser;
    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(UUID.randomUUID());

        authenticatedUser = new User();
        authenticatedUser.setId(UUID.randomUUID());
        authenticatedUser.setEmail("admin@teste.com");
        authenticatedUser.setPassword("encoded");
        authenticatedUser.setRole(Roles.ADMIN);
        authenticatedUser.setCompany(company);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setName("Func");
        user.setEmail("func@teste.com");
        user.setRole(Roles.EMPLOYEE);
        user.setCompany(company);
        user.setPassword("old_hash");

        UserUpdateRequest request = new UserUpdateRequest(id, "Func Novo", "funcnovo@teste.com", null, Roles.EMPLOYEE);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = updateUserService.execute(request);

        assertNotNull(response);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserUpdateRequest request = new UserUpdateRequest(id, "Nome", "email@teste.com", null, Roles.EMPLOYEE);
        assertThrows(ResourceNotFoundException.class, () -> updateUserService.execute(request));
    }

    @Test
    void shouldThrowForbiddenExceptionWhenDifferentCompany() {
        UUID id = UUID.randomUUID();

        Company otherCompany = new Company();
        otherCompany.setId(UUID.randomUUID());

        User user = new User();
        user.setId(id);
        user.setCompany(otherCompany);
        user.setRole(Roles.EMPLOYEE);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserUpdateRequest request = new UserUpdateRequest(id, "Nome", "email@teste.com", null, Roles.EMPLOYEE);
        assertThrows(ForbiddenException.class, () -> updateUserService.execute(request));
    }

    @Test
    void shouldThrowInvalidRoleExceptionForDeveloperRole() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setCompany(company);
        user.setRole(Roles.EMPLOYEE);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserUpdateRequest request = new UserUpdateRequest(id, "Nome", "email@teste.com", null, Roles.DEVELOPER);
        assertThrows(InvalidRoleException.class, () -> updateUserService.execute(request));
    }
}
