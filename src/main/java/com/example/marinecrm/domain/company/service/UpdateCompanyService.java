package com.example.marinecrm.domain.company.service;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.company.CompanyRepository;
import com.example.marinecrm.domain.company.DTO.CompanyResponse;
import com.example.marinecrm.domain.company.DTO.CompanyUpdateRequest;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCompanyService implements Command<CompanyUpdateRequest, CompanyResponse> {

    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public CompanyResponse execute(CompanyUpdateRequest request) {
        Company company = companyRepository.findById(request.id()).orElseThrow(() ->
                new ResourceNotFoundException("Empresa não encontrada: " + request.id()));

        company.update(request);

        Company saved = companyRepository.save(company);

        return new CompanyResponse(saved);
    }
}
