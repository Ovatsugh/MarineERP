package com.example.marinecrm.domain.product.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.product.Product;
import com.example.marinecrm.domain.product.ProductRepository;
import com.example.marinecrm.domain.product.DTO.ProductRequest;
import com.example.marinecrm.domain.product.DTO.ProductResponse;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProductByIdServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductByIdService getProductByIdService;

    private User user;
    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company();
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
    void shouldReturnProductWhenFound() {
        UUID productId = UUID.randomUUID();
        ProductRequest request = new ProductRequest("Pneu", new BigDecimal("150.00"), 10, "Honda CG 160", null, null);
        Product product = new Product(request, user);
        product.setId(productId);

        when(productRepository.findByIdAndUser_Company_Id(productId, company.getId()))
                .thenReturn(Optional.of(product));

        ProductResponse response = getProductByIdService.execute(productId);

        assertNotNull(response);
        assertEquals(productId, response.id());
        assertEquals("Pneu", response.name());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();

        when(productRepository.findByIdAndUser_Company_Id(productId, company.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getProductByIdService.execute(productId));
    }
}
