package com.example.marinecrm.domain.sale;

import com.example.marinecrm.domain.customer.DTO.CustomerResponse;
import com.example.marinecrm.domain.sale.DTO.ItemSaleRequest;
import com.example.marinecrm.domain.sale.DTO.SalesRequest;
import com.example.marinecrm.domain.sale.DTO.SalesResponse;
import com.example.marinecrm.domain.sale.services.*;
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
class SalesControllerTest {

    @Mock private CreateSaleService createSaleService;
    @Mock private DeleteSaleService deleteSaleService;
    @Mock private GetAllSaleService getAllSaleService;
    @Mock private GetSaleByIdService getSaleByIdService;
    @Mock private UpdateSaleService updateSaleService;

    @InjectMocks private SalesController salesController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new SpringDataJacksonConfiguration.PageModule(
                    new SpringDataWebSettings(EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)));

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(salesController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createSale_shouldReturn200() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        SalesRequest request = new SalesRequest(customerId, "Venda teste", List.of(new ItemSaleRequest(productId, 2)));

        CustomerResponse customerResponse = new CustomerResponse(customerId, "João", "11999991111", "12345678900");
        SalesResponse response = new SalesResponse(UUID.randomUUID(), customerResponse, new BigDecimal("300.00"), "Venda teste");

        when(createSaleService.execute(any())).thenReturn(response);

        mockMvc.perform(post("/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(300.00));
    }

    @Test
    void getSales_shouldReturn200WithPage() throws Exception {
        UUID customerId = UUID.randomUUID();
        CustomerResponse customerResponse = new CustomerResponse(customerId, "Maria", "11988887777", "98765432100");
        SalesResponse sale = new SalesResponse(UUID.randomUUID(), customerResponse, new BigDecimal("150.00"), null);

        when(getAllSaleService.execute(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(sale)));

        mockMvc.perform(get("/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(150.00));
    }

    @Test
    void getSaleById_shouldReturn200WhenFound() throws Exception {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        CustomerResponse customerResponse = new CustomerResponse(customerId, "João", "11999991111", "12345678900");
        SalesResponse response = new SalesResponse(id, customerResponse, new BigDecimal("200.00"), null);

        when(getSaleByIdService.execute(id)).thenReturn(response);

        mockMvc.perform(get("/sales/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(200.00));
    }

    @Test
    void getSaleById_shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(getSaleByIdService.execute(id)).thenThrow(new ResourceNotFoundException("Venda não encontrada"));

        mockMvc.perform(get("/sales/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSale_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/sales/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }
}
