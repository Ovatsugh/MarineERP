package com.example.marinecrm.domain.customer;

import com.example.marinecrm.domain.customer.DTO.CustomerRequest;
import com.example.marinecrm.domain.customer.DTO.CustomerResponse;
import com.example.marinecrm.domain.customer.service.*;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock private CreateCustomerService createCustomerService;
    @Mock private DeleteCustomerService deleteCustomerService;
    @Mock private GetAllCustomerService getAllCustomerService;
    @Mock private GetCustomerByIdService getCustomerByIdService;
    @Mock private UpdateCustomerService updateCustomerService;

    @InjectMocks private CustomerController customerController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new SpringDataJacksonConfiguration.PageModule(
                    new SpringDataWebSettings(EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)));

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createCustomer_shouldReturn200() throws Exception {
        CustomerRequest request = new CustomerRequest("João Silva", "12345678900", "11987654321");
        CustomerResponse response = new CustomerResponse(UUID.randomUUID(), "João Silva", "11987654321", "12345678900");

        when(createCustomerService.execute(any())).thenReturn(response);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva"));
    }

    @Test
    void getCustomers_shouldReturn200WithPage() throws Exception {
        CustomerResponse customer = new CustomerResponse(UUID.randomUUID(), "Maria", "11999998888", "98765432100");

        when(getAllCustomerService.execute(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(customer)));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Maria"));
    }

    @Test
    void getCustomerById_shouldReturn200WhenFound() throws Exception {
        UUID id = UUID.randomUUID();
        CustomerResponse response = new CustomerResponse(id, "Pedro", "11988887777", "11122233344");

        when(getCustomerByIdService.execute(id)).thenReturn(response);

        mockMvc.perform(get("/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pedro"));
    }

    @Test
    void getCustomerById_shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(getCustomerByIdService.execute(id)).thenThrow(new ResourceNotFoundException("Cliente não encontrado"));

        mockMvc.perform(get("/customers/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCustomer_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/customers/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateCustomer_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        CustomerRequest request = new CustomerRequest("João Atualizado", "12345678900", "11988887777");
        CustomerResponse response = new CustomerResponse(id, "João Atualizado", "11988887777", "12345678900");

        when(updateCustomerService.execute(any())).thenReturn(response);

        mockMvc.perform(put("/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Atualizado"));
    }
}
