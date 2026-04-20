package com.example.marinecrm.domain.user.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.DTO.UserResponse;
import com.example.marinecrm.domain.user.Services.GetMeService;
import com.example.marinecrm.enums.Roles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetMeServiceTest {

    @InjectMocks private GetMeService getMeService;

    private User user;

    @BeforeEach
    void setUp() {
        Company company = new Company();
        company.setId(UUID.randomUUID());

        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Admin");
        user.setEmail("admin@teste.com");
        user.setPassword("encoded");
        user.setRole(Roles.ADMIN);
        user.setCompany(company);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAuthenticatedUser() {
        UserResponse response = getMeService.execute(null);

        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals("Admin", response.name());
        assertEquals("admin@teste.com", response.email());
        assertEquals(Roles.ADMIN, response.role());
    }
}
