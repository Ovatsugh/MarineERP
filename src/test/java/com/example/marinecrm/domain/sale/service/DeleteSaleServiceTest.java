package com.example.marinecrm.domain.sale.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.customer.Customer;
import com.example.marinecrm.domain.customer.DTO.CustomerRequest;
import com.example.marinecrm.domain.sale.Sale;
import com.example.marinecrm.domain.sale.SaleRepository;
import com.example.marinecrm.domain.sale.DTO.SalesRequest;
import com.example.marinecrm.domain.sale.services.DeleteSaleService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteSaleServiceTest {

    @Mock private SaleRepository saleRepository;
    @InjectMocks private DeleteSaleService deleteSaleService;

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
    void shouldDeleteSaleSuccessfully() {
        UUID saleId = UUID.randomUUID();
        Sale sale = new Sale(new SalesRequest(customer.getId(), null, List.of()), user, customer, new BigDecimal("150.00"));
        sale.setId(saleId);

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));

        deleteSaleService.execute(saleId);

        verify(saleRepository).delete(sale);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenNotFound() {
        UUID saleId = UUID.randomUUID();
        when(saleRepository.findById(saleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> deleteSaleService.execute(saleId));
        verify(saleRepository, never()).delete(any());
    }

    @Test
    void shouldThrowForbiddenExceptionWhenDifferentCompany() {
        UUID saleId = UUID.randomUUID();

        Company otherCompany = new Company();
        otherCompany.setId(UUID.randomUUID());
        User otherUser = new User();
        otherUser.setCompany(otherCompany);
        otherUser.setRole(Roles.ADMIN);

        Customer otherCustomer = new Customer(new CustomerRequest("Maria", "99988877766", "11988887777"), otherUser);
        Sale sale = new Sale(new SalesRequest(otherCustomer.getId(), null, List.of()), otherUser, otherCustomer, new BigDecimal("200.00"));
        sale.setId(saleId);

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));

        assertThrows(ForbiddenException.class, () -> deleteSaleService.execute(saleId));
        verify(saleRepository, never()).delete(any());
    }
}
