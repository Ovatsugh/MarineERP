package com.example.marinecrm.domain.customer.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.customer.Customer;
import com.example.marinecrm.domain.customer.CustomerRepository;
import com.example.marinecrm.domain.customer.DTO.CustomerRequest;
import com.example.marinecrm.domain.customer.DTO.CustomerResponse;
import com.example.marinecrm.domain.user.User;
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
class GetCustomerByIdServiceTest {

    @Mock private CustomerRepository customerRepository;
    @InjectMocks private GetCustomerByIdService getCustomerByIdService;

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
    void shouldReturnCustomerWhenFound() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(new CustomerRequest("Maria", "98765432100", "11999998888"), user);
        customer.setId(id);

        when(customerRepository.findByIdAndUser_Company_Id(id, company.getId()))
                .thenReturn(Optional.of(customer));

        CustomerResponse response = getCustomerByIdService.execute(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Maria", response.name());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findByIdAndUser_Company_Id(id, company.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getCustomerByIdService.execute(id));
    }
}
