package com.example.marinecrm.domain.sale.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.customer.Customer;
import com.example.marinecrm.domain.customer.DTO.CustomerRequest;
import com.example.marinecrm.domain.sale.Sale;
import com.example.marinecrm.domain.sale.SaleRepository;
import com.example.marinecrm.domain.sale.DTO.SalesRequest;
import com.example.marinecrm.domain.sale.DTO.SalesResponse;
import com.example.marinecrm.domain.sale.services.GetSaleByIdService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSaleByIdServiceTest {

    @Mock private SaleRepository saleRepository;
    @InjectMocks private GetSaleByIdService getSaleByIdService;

    private User user;
    private Company company;
    private Customer customer;

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

        customer = new Customer(new CustomerRequest("João", "12345678900", "11999991111"), user);
        customer.setId(UUID.randomUUID());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnSaleWhenFound() {
        UUID saleId = UUID.randomUUID();
        SalesRequest request = new SalesRequest(customer.getId(), "Nota", List.of());
        Sale sale = new Sale(request, user, customer, new BigDecimal("300.00"));
        sale.setId(saleId);

        when(saleRepository.findByIdAndUser_Company_Id(saleId, company.getId()))
                .thenReturn(Optional.of(sale));

        SalesResponse response = getSaleByIdService.execute(saleId);

        assertNotNull(response);
        assertEquals(saleId, response.id());
        assertEquals(new BigDecimal("300.00"), response.amount());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenNotFound() {
        UUID saleId = UUID.randomUUID();
        when(saleRepository.findByIdAndUser_Company_Id(saleId, company.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getSaleByIdService.execute(saleId));
    }
}
