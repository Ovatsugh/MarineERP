package com.example.marinecrm.domain.user.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.UserRepository;
import com.example.marinecrm.domain.user.DTO.UserRequest;
import com.example.marinecrm.domain.user.DTO.UserResponse;
import com.example.marinecrm.domain.user.Services.CreateUserService;
import com.example.marinecrm.enums.Roles;
import com.example.marinecrm.exceptions.InvalidRoleException;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private CreateUserService createUserService;

    private User authenticatedUser;

    @BeforeEach
    void setUp() {
        Company company = new Company();
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
    void shouldCreateEmployeeUserSuccessfully() {
        UserRequest request = new UserRequest("Funcionário", "func@teste.com", "senha123", Roles.EMPLOYEE);

        User saved = new User(request, "hashed", authenticatedUser.getCompany());
        saved.setId(UUID.randomUUID());

        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = createUserService.execute(request);

        assertNotNull(response);
        assertEquals("Funcionário", response.name());
        assertEquals(Roles.EMPLOYEE, response.role());
    }

    @Test
    void shouldCreateAdminUserSuccessfully() {
        UserRequest request = new UserRequest("Admin", "admin2@teste.com", "senha123", Roles.ADMIN);

        User saved = new User(request, "hashed", authenticatedUser.getCompany());
        saved.setId(UUID.randomUUID());

        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = createUserService.execute(request);

        assertEquals(Roles.ADMIN, response.role());
    }

    @Test
    void shouldThrowInvalidRoleExceptionForDeveloperRole() {
        UserRequest request = new UserRequest("Dev", "dev@teste.com", "senha123", Roles.DEVELOPER);

        assertThrows(InvalidRoleException.class, () -> createUserService.execute(request));
        verify(userRepository, never()).save(any());
    }
}
