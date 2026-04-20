package com.example.marinecrm.domain.company;

import com.example.marinecrm.domain.company.DTO.CompanyRequest;
import com.example.marinecrm.domain.company.DTO.CompanyResponse;
import com.example.marinecrm.domain.company.service.*;
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
class CompanyControllerTest {

    @Mock private CreateCompanyService createCompanyService;
    @Mock private DeleteCompanyService deleteCompanyService;
    @Mock private GetAllCompanyService getAllCompanyService;
    @Mock private GetCompanyByIdService getCompanyByIdService;
    @Mock private UpdateCompanyService updateCompanyService;

    @InjectMocks private CompanyController companyController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new SpringDataJacksonConfiguration.PageModule(
                    new SpringDataWebSettings(EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)));

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(companyController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createCompany_shouldReturn200() throws Exception {
        CompanyRequest request = new CompanyRequest("Marine Motors", "12345678000190");
        CompanyResponse response = new CompanyResponse(UUID.randomUUID(), "Marine Motors", "12345678000190");

        when(createCompanyService.execute(any())).thenReturn(response);

        mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Marine Motors"));
    }

    @Test
    void getCompanies_shouldReturn200WithPage() throws Exception {
        CompanyResponse company = new CompanyResponse(UUID.randomUUID(), "Marine Motors", "12345678000190");

        when(getAllCompanyService.execute(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(company)));

        mockMvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Marine Motors"));
    }

    @Test
    void getCompanyById_shouldReturn200WhenFound() throws Exception {
        UUID id = UUID.randomUUID();
        CompanyResponse response = new CompanyResponse(id, "Marine Motors", "12345678000190");

        when(getCompanyByIdService.execute(id)).thenReturn(response);

        mockMvc.perform(get("/companies/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Marine Motors"));
    }

    @Test
    void getCompanyById_shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(getCompanyByIdService.execute(id)).thenThrow(new ResourceNotFoundException("Empresa não encontrada"));

        mockMvc.perform(get("/companies/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCompany_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/companies/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateCompany_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        CompanyRequest request = new CompanyRequest("Marine Atualizado", "98765432000191");
        CompanyResponse response = new CompanyResponse(id, "Marine Atualizado", "98765432000191");

        when(updateCompanyService.execute(any())).thenReturn(response);

        mockMvc.perform(put("/companies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Marine Atualizado"));
    }
}
