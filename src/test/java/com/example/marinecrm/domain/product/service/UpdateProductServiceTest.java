package com.example.marinecrm.domain.product.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.product.Product;
import com.example.marinecrm.domain.product.ProductRepository;
import com.example.marinecrm.domain.product.DTO.ProductRequest;
import com.example.marinecrm.domain.product.DTO.ProductResponse;
import com.example.marinecrm.domain.product.DTO.ProductUpdateRequest;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductService updateProductService;

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
    void shouldUpdateProductSuccessfully() {
        UUID productId = UUID.randomUUID();
        ProductRequest originalRequest = new ProductRequest("Pneu", new BigDecimal("100.00"), 5, "Honda CG", null, null);
        Product product = new Product(originalRequest, user);
        product.setId(productId);

        ProductRequest updatePayload = new ProductRequest("Pneu Reforçado", new BigDecimal("180.00"), 8, "Honda CG 160", "Novo modelo", "PN-002");
        ProductUpdateRequest updateRequest = new ProductUpdateRequest(productId, updatePayload);

        Product updated = new Product(updatePayload, user);
        updated.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updated);

        ProductResponse response = updateProductService.execute(updateRequest);

        assertEquals("Pneu Reforçado", response.name());
        assertEquals(new BigDecimal("180.00"), response.price());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();
        ProductRequest payload = new ProductRequest("Pneu", new BigDecimal("100.00"), 5, "Honda CG", null, null);
        ProductUpdateRequest request = new ProductUpdateRequest(productId, payload);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> updateProductService.execute(request));
    }

    @Test
    void shouldThrowForbiddenExceptionWhenUserBelongsToDifferentCompany() {
        UUID productId = UUID.randomUUID();

        Company otherCompany = new Company();
        otherCompany.setId(UUID.randomUUID());

        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setCompany(otherCompany);
        otherUser.setRole(Roles.ADMIN);

        ProductRequest originalRequest = new ProductRequest("Pneu", new BigDecimal("100.00"), 5, "Honda CG", null, null);
        Product product = new Product(originalRequest, otherUser);
        product.setId(productId);

        ProductRequest payload = new ProductRequest("Pneu Novo", new BigDecimal("120.00"), 3, "Honda CG", null, null);
        ProductUpdateRequest request = new ProductUpdateRequest(productId, payload);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(ForbiddenException.class, () -> updateProductService.execute(request));
    }
}
