package com.example.marinecrm.domain.company.service;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.company.CompanyRepository;
import com.example.marinecrm.domain.company.DTO.CompanyResponse;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCompanyByIdService implements Command<UUID, CompanyResponse> {

    private final CompanyRepository companyRepository;

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse execute(UUID id) {
        Company company = companyRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Empresa não encontrada: " + id));

        return new CompanyResponse(company);
    }
}
