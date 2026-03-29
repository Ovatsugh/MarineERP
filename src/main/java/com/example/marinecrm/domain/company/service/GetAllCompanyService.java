package com.example.marinecrm.domain.company.service;

import com.example.marinecrm.Query;
import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.company.CompanyRepository;
import com.example.marinecrm.domain.company.DTO.CompanyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllCompanyService implements Query<Pageable, Page<CompanyResponse>> {

    private final CompanyRepository companyRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyResponse> execute(Pageable pageable) {
        Page<Company> companiesPage = companyRepository.findAll(pageable);

        return companiesPage.map(CompanyResponse::new);
    }
}
