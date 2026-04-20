package com.example.marinecrm.domain.customer.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.customer.Customer;
import com.example.marinecrm.domain.customer.CustomerRepository;
import com.example.marinecrm.domain.customer.DTO.CustomerRequest;
import com.example.marinecrm.domain.customer.DTO.CustomerResponse;
import com.example.marinecrm.domain.customer.DTO.CustomerUpdateRequest;
import com.example.marinecrm.domain.user.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    @InjectMocks private UpdateCustomerService updateCustomerService;

    private User user;
    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company();
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
    void shouldUpdateCustomerSuccessfully() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(new CustomerRequest("João", "12345678900", "11999991111"), user);
        customer.setId(id);

        CustomerRequest payload = new CustomerRequest("João Atualizado", "12345678900", "11988887777");
        CustomerUpdateRequest request = new CustomerUpdateRequest(id, payload);

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponse response = updateCustomerService.execute(request);

        assertNotNull(response);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        CustomerUpdateRequest request = new CustomerUpdateRequest(id,
                new CustomerRequest("Nome", "12345678900", "11999991111"));

        assertThrows(ResourceNotFoundException.class, () -> updateCustomerService.execute(request));
    }

    @Test
    void shouldThrowForbiddenExceptionWhenDifferentCompany() {
        UUID id = UUID.randomUUID();

        Company otherCompany = new Company();
        otherCompany.setId(UUID.randomUUID());
        User otherUser = new User();
        otherUser.setCompany(otherCompany);
        otherUser.setRole(Roles.ADMIN);

        Customer customer = new Customer(new CustomerRequest("João", "12345678900", "11999991111"), otherUser);
        customer.setId(id);

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(id,
                new CustomerRequest("João Novo", "12345678900", "11988887777"));

        assertThrows(ForbiddenException.class, () -> updateCustomerService.execute(request));
    }
}
