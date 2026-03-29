package com.example.marinecrm.domain.company.service;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.company.CompanyRepository;
import com.example.marinecrm.domain.company.DTO.CompanyRequest;
import com.example.marinecrm.domain.company.DTO.CompanyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCompanyService implements Command<CompanyRequest, CompanyResponse> {

    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public CompanyResponse execute(CompanyRequest request) {
        Company company = new Company(request);

        Company saved = companyRepository.save(company);

        return new CompanyResponse(saved);
    }
}
