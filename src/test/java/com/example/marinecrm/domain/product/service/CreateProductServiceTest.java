package com.example.marinecrm.domain.product.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.product.Product;
import com.example.marinecrm.domain.product.ProductRepository;
import com.example.marinecrm.domain.product.DTO.ProductRequest;
import com.example.marinecrm.domain.product.DTO.ProductResponse;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateProductService createProductService;

    private User user;

    @BeforeEach
    void setUp() {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        company.setName("Empresa Teste");

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("admin@teste.com");
        user.setPassword("encoded");
        user.setRole(Roles.ADMIN);
        user.setCompany(company);

        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCreateProductSuccessfully() {
        ProductRequest request = new ProductRequest("Pneu Dianteiro", new BigDecimal("150.00"), 10, "Honda CG 160", "Pneu traseiro reforçado", "PN-001");

        Product saved = new Product(request, user);
        saved.setId(UUID.randomUUID());

        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse response = createProductService.execute(request);

        assertNotNull(response);
        assertEquals("Pneu Dianteiro", response.name());
        assertEquals(new BigDecimal("150.00"), response.price());
        assertEquals(10, response.stock_quantity());
        assertEquals("Honda CG 160", response.bikeModel());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldMapAllFieldsFromRequest() {
        ProductRequest request = new ProductRequest("Corrente", new BigDecimal("89.90"), 5, "Yamaha Factor 150", "Corrente reforçada", "CR-002");

        Product saved = new Product(request, user);
        saved.setId(UUID.randomUUID());

        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse response = createProductService.execute(request);

        assertEquals("CR-002", response.code());
        assertEquals("Corrente reforçada", response.description());
    }
}
