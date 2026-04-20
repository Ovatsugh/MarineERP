package com.example.marinecrm.domain.company.service;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.company.CompanyRepository;
import com.example.marinecrm.domain.company.DTO.CompanyRequest;
import com.example.marinecrm.domain.company.DTO.CompanyResponse;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCompanyByIdServiceTest {

    @Mock private CompanyRepository companyRepository;
    @InjectMocks private GetCompanyByIdService getCompanyByIdService;

    @Test
    void shouldReturnCompanyWhenFound() {
        UUID id = UUID.randomUUID();
        Company company = new Company(new CompanyRequest("Marine Motors", "12345678000190"));
        company.setId(id);

        when(companyRepository.findById(id)).thenReturn(Optional.of(company));

        CompanyResponse response = getCompanyByIdService.execute(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Marine Motors", response.name());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getCompanyByIdService.execute(id));
    }
}
