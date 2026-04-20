package com.example.marinecrm.domain.product;

import com.example.marinecrm.domain.product.DTO.ProductRequest;
import com.example.marinecrm.domain.product.DTO.ProductResponse;
import com.example.marinecrm.domain.product.service.*;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.SpringDataJacksonConfiguration;
import org.springframework.data.web.config.SpringDataWebSettings;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock private CreateProductService createProductService;
    @Mock private DeleteProductService deleteProductService;
    @Mock private GetAllProductService getAllProductService;
    @Mock private GetProductByIdService getProductByIdService;
    @Mock private UpdateProductService updateProductService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new SpringDataJacksonConfiguration.PageModule(
                    new SpringDataWebSettings(EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)));

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createProduct_shouldReturn200WithValidBody() throws Exception {
        ProductRequest request = new ProductRequest("Pneu Dianteiro", new BigDecimal("150.00"), 10, "Honda CG 160", null, "PN-001");
        ProductResponse response = new ProductResponse(UUID.randomUUID(), "Pneu Dianteiro", new BigDecimal("150.00"), 10, "Honda CG 160", "PN-001", null);

        when(createProductService.execute(any())).thenReturn(response);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pneu Dianteiro"))
                .andExpect(jsonPath("$.price").value(150.00));
    }

    @Test
    void getProducts_shouldReturn200WithPage() throws Exception {
        ProductResponse product = new ProductResponse(UUID.randomUUID(), "Pneu", new BigDecimal("100.00"), 5, "Honda CG", null, null);

        when(getAllProductService.execute(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(product)));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Pneu"));
    }

    @Test
    void getProductById_shouldReturn200WhenFound() throws Exception {
        UUID id = UUID.randomUUID();
        ProductResponse response = new ProductResponse(id, "Corrente", new BigDecimal("89.90"), 3, "Yamaha Factor", "CR-001", null);

        when(getProductByIdService.execute(id)).thenReturn(response);

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Corrente"));
    }

    @Test
    void getProductById_shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(getProductByIdService.execute(id)).thenThrow(new ResourceNotFoundException("Produto não encontrado: " + id));

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_shouldReturn204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateProduct_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        ProductRequest request = new ProductRequest("Pneu Atualizado", new BigDecimal("200.00"), 7, "Honda CG 160", null, null);
        ProductResponse response = new ProductResponse(id, "Pneu Atualizado", new BigDecimal("200.00"), 7, "Honda CG 160", null, null);

        when(updateProductService.execute(any())).thenReturn(response);

        mockMvc.perform(put("/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pneu Atualizado"));
    }
}
