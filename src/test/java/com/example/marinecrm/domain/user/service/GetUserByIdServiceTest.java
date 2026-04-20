package com.example.marinecrm.domain.user.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.UserRepository;
import com.example.marinecrm.domain.user.DTO.UserResponse;
import com.example.marinecrm.domain.user.Services.GetUserByIdService;
import com.example.marinecrm.enums.Roles;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByIdServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private GetUserByIdService getUserByIdService;

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
    void shouldReturnUserWhenFound() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setName("Funcionário");
        user.setEmail("func@teste.com");
        user.setRole(Roles.EMPLOYEE);
        user.setCompany(company);

        when(userRepository.findByIdAndCompany_Id(id, company.getId())).thenReturn(Optional.of(user));

        UserResponse response = getUserByIdService.execute(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Funcionário", response.name());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findByIdAndCompany_Id(id, company.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getUserByIdService.execute(id));
    }
}
