package com.example.marinecrm.domain.customer.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.customer.Customer;
import com.example.marinecrm.domain.customer.CustomerRepository;
import com.example.marinecrm.domain.customer.DTO.CustomerRequest;
import com.example.marinecrm.domain.customer.DTO.CustomerResponse;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.enums.Roles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    @InjectMocks private CreateCustomerService createCustomerService;

    private User user;

    @BeforeEach
    void setUp() {
        Company company = new Company();
        company.setId(UUID.randomUUID());

        user = new User();
        user.setId(UUID.randomUUID());
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
    void shouldCreateCustomerSuccessfully() {
        CustomerRequest request = new CustomerRequest("João Silva", "12345678900", "11987654321");

        Customer saved = new Customer(request, user);
        saved.setId(UUID.randomUUID());

        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        CustomerResponse response = createCustomerService.execute(request);

        assertNotNull(response);
        assertEquals("João Silva", response.name());
        assertEquals("12345678900", response.cpf());
        verify(customerRepository).save(any(Customer.class));
    }
}
