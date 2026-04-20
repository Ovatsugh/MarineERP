package com.example.marinecrm.domain.user.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.UserRepository;
import com.example.marinecrm.domain.user.Services.DeleteUserService;
import com.example.marinecrm.enums.Roles;
import com.example.marinecrm.exceptions.ForbiddenException;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private DeleteUserService deleteUserService;

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
    void shouldDeleteUserSuccessfully() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setCompany(company);
        user.setRole(Roles.EMPLOYEE);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        deleteUserService.execute(id);

        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> deleteUserService.execute(id));
        verify(userRepository, never()).delete(any());
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

        assertThrows(ForbiddenException.class, () -> deleteUserService.execute(id));
        verify(userRepository, never()).delete(any());
    }
}
