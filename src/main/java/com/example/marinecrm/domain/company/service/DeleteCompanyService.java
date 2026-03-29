package com.example.marinecrm.domain.company.service;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.company.CompanyRepository;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCompanyService implements Command<UUID, Void> {

    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public Void execute(UUID id) {
        Company company = companyRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Empresa não encontrada: " + id));

        companyRepository.delete(company);
        return null;
    }
}
