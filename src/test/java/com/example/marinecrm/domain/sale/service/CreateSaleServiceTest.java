package com.example.marinecrm.domain.sale.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.customer.Customer;
import com.example.marinecrm.domain.customer.CustomerRepository;
import com.example.marinecrm.domain.customer.DTO.CustomerRequest;
import com.example.marinecrm.domain.product.Product;
import com.example.marinecrm.domain.product.ProductRepository;
import com.example.marinecrm.domain.product.DTO.ProductRequest;
import com.example.marinecrm.domain.sale.ItemSale;
import com.example.marinecrm.domain.sale.ItemSaleRepository;
import com.example.marinecrm.domain.sale.Sale;
import com.example.marinecrm.domain.sale.SaleRepository;
import com.example.marinecrm.domain.sale.DTO.ItemSaleRequest;
import com.example.marinecrm.domain.sale.DTO.SalesRequest;
import com.example.marinecrm.domain.sale.DTO.SalesResponse;
import com.example.marinecrm.domain.sale.services.CreateSaleService;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.enums.Roles;
import com.example.marinecrm.exceptions.InsufficientStockException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSaleServiceTest {

    @Mock private SaleRepository saleRepository;
    @Mock private ItemSaleRepository itemSaleRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ProductRepository productRepository;
    @InjectMocks private CreateSaleService createSaleService;

    private User user;
    private Customer customer;
    private Product product;

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

        customer = new Customer(new CustomerRequest("João", "12345678900", "11999991111"), user);
        customer.setId(UUID.randomUUID());

        product = new Product(new ProductRequest("Pneu", new BigDecimal("150.00"), 10, "Honda CG", null, null), user);
        product.setId(UUID.randomUUID());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCreateSaleSuccessfully() {
        SalesRequest request = new SalesRequest(customer.getId(), "Venda teste",
                List.of(new ItemSaleRequest(product.getId(), 2)));

        Sale sale = new Sale(request, user, customer, new BigDecimal("300.00"));
        sale.setId(UUID.randomUUID());

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);
        when(itemSaleRepository.save(any(ItemSale.class))).thenAnswer(inv -> inv.getArgument(0));

        SalesResponse response = createSaleService.execute(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("300.00"), response.amount());
        verify(productRepository).save(any(Product.class));
        verify(saleRepository).save(any(Sale.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCustomerNotFound() {
        UUID unknownCustomerId = UUID.randomUUID();
        SalesRequest request = new SalesRequest(unknownCustomerId, null,
                List.of(new ItemSaleRequest(product.getId(), 1)));

        when(customerRepository.findById(unknownCustomerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> createSaleService.execute(request));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void shouldThrowInsufficientStockExceptionWhenStockIsLow() {
        product.setStock_quantity(1);
        SalesRequest request = new SalesRequest(customer.getId(), null,
                List.of(new ItemSaleRequest(product.getId(), 5)));

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> createSaleService.execute(request));
        verify(saleRepository, never()).save(any());
    }
}
