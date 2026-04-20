package com.example.marinecrm.domain.company.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.company.CompanyRepository;
import com.example.marinecrm.domain.company.DTO.CompanyRequest;
import com.example.marinecrm.domain.company.DTO.CompanyResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCompanyServiceTest {

    @Mock private CompanyRepository companyRepository;
    @InjectMocks private CreateCompanyService createCompanyService;

    @Test
    void shouldCreateCompanySuccessfully() {
        CompanyRequest request = new CompanyRequest("Marine Motors", "12345678000190");

        Company saved = new Company(request);
        saved.setId(UUID.randomUUID());

        when(companyRepository.save(any(Company.class))).thenReturn(saved);

        CompanyResponse response = createCompanyService.execute(request);

        assertNotNull(response);
        assertEquals("Marine Motors", response.name());
        assertEquals("12345678000190", response.cnpj());
        verify(companyRepository).save(any(Company.class));
    }
}
