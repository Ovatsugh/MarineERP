package com.example.marinecrm.domain.auth.service;

import com.example.marinecrm.domain.auth.DTO.AuthRequest;
import com.example.marinecrm.domain.auth.DTO.AuthResponse;
import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.company.CompanyRepository;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.UserRepository;
import com.example.marinecrm.enums.Roles;
import com.example.marinecrm.exceptions.EmailAlreadyExistsException;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @InjectMocks private RegisterService registerService;

    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(UUID.randomUUID());
        company.setName("Marine Motors");
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        AuthRequest request = new AuthRequest("João", "joao@teste.com", "senha123", company.getId());

        when(userRepository.existsByEmail("joao@teste.com")).thenReturn(false);
        when(companyRepository.findById(company.getId())).thenReturn(Optional.of(company));
        when(passwordEncoder.encode("senha123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt.token");

        AuthResponse response = registerService.execute(request);

        assertNotNull(response);
        assertEquals("jwt.token", response.token());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowEmailAlreadyExistsExceptionWhenEmailInUse() {
        AuthRequest request = new AuthRequest("João", "joao@teste.com", "senha123", company.getId());
        when(userRepository.existsByEmail("joao@teste.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> registerService.execute(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCompanyNotFound() {
        UUID unknownId = UUID.randomUUID();
        AuthRequest request = new AuthRequest("João", "joao@teste.com", "senha123", unknownId);

        when(userRepository.existsByEmail("joao@teste.com")).thenReturn(false);
        when(companyRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> registerService.execute(request));
        verify(userRepository, never()).save(any());
    }
}
