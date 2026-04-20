package com.example.marinecrm.domain.auth.service;

import com.example.marinecrm.domain.auth.DTO.AuthResponse;
import com.example.marinecrm.domain.auth.DTO.LoginRequest;
import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.UserRepository;
import com.example.marinecrm.enums.Roles;
import com.example.marinecrm.exceptions.InvalidCredentialsException;
import com.example.marinecrm.infra.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @InjectMocks private LoginService loginService;

    private User user;

    @BeforeEach
    void setUp() {
        Company company = new Company();
        company.setId(UUID.randomUUID());

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("admin@teste.com");
        user.setPassword("hashed_password");
        user.setRole(Roles.ADMIN);
        user.setCompany(company);
    }

    @Test
    void shouldReturnTokenOnValidCredentials() {
        LoginRequest request = new LoginRequest("admin@teste.com", "senha123");

        when(userRepository.findByEmail("admin@teste.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha123", "hashed_password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt.token.aqui");

        AuthResponse response = loginService.execute(request);

        assertNotNull(response);
        assertEquals("jwt.token.aqui", response.token());
    }

    @Test
    void shouldThrowInvalidCredentialsExceptionWhenUserNotFound() {
        LoginRequest request = new LoginRequest("naoexiste@teste.com", "senha123");
        when(userRepository.findByEmail("naoexiste@teste.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> loginService.execute(request));
    }

    @Test
    void shouldThrowInvalidCredentialsExceptionWhenPasswordIsWrong() {
        LoginRequest request = new LoginRequest("admin@teste.com", "senha_errada");

        when(userRepository.findByEmail("admin@teste.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha_errada", "hashed_password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> loginService.execute(request));
    }
}
