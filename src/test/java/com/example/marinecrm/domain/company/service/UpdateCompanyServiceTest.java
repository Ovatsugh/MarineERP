package com.example.marinecrm.domain.company.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.company.CompanyRepository;
import com.example.marinecrm.domain.company.DTO.CompanyRequest;
import com.example.marinecrm.domain.company.DTO.CompanyResponse;
import com.example.marinecrm.domain.company.DTO.CompanyUpdateRequest;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCompanyServiceTest {

    @Mock private CompanyRepository companyRepository;
    @InjectMocks private UpdateCompanyService updateCompanyService;

    @Test
    void shouldUpdateCompanySuccessfully() {
        UUID id = UUID.randomUUID();
        Company company = new Company(new CompanyRequest("Marine Motors", "12345678000190"));
        company.setId(id);

        CompanyRequest payload = new CompanyRequest("Marine Motors Atualizado", "98765432000191");
        CompanyUpdateRequest request = new CompanyUpdateRequest(id, payload);

        when(companyRepository.findById(id)).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        CompanyResponse response = updateCompanyService.execute(request);

        assertNotNull(response);
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        CompanyUpdateRequest request = new CompanyUpdateRequest(id,
                new CompanyRequest("Nome", "12345678000190"));

        assertThrows(ResourceNotFoundException.class, () -> updateCompanyService.execute(request));
    }
}
