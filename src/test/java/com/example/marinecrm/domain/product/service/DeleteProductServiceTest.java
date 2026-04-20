package com.example.marinecrm.domain.product.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.product.Product;
import com.example.marinecrm.domain.product.ProductRepository;
import com.example.marinecrm.domain.product.DTO.ProductRequest;
import com.example.marinecrm.domain.sale.ItemSaleRepository;
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
class DeleteProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ItemSaleRepository itemSaleRepository;

    @InjectMocks
    private DeleteProductService deleteProductService;

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
    void shouldDeleteProductSuccessfully() {
        UUID productId = UUID.randomUUID();
        ProductRequest request = new ProductRequest("Pneu", new BigDecimal("150.00"), 10, "Honda CG", null, null);
        Product product = new Product(request, user);
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(itemSaleRepository.findByProduct_Id(productId)).thenReturn(List.of());

        deleteProductService.execute(productId);

        verify(productRepository).delete(product);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> deleteProductService.execute(productId));
        verify(productRepository, never()).delete(any());
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

        ProductRequest request = new ProductRequest("Pneu", new BigDecimal("150.00"), 10, "Honda CG", null, null);
        Product product = new Product(request, otherUser);
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(ForbiddenException.class, () -> deleteProductService.execute(productId));
        verify(productRepository, never()).delete(any());
    }
}
