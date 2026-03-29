package com.example.marinecrm.domain.company;

import com.example.marinecrm.domain.company.DTO.CompanyRequest;
import com.example.marinecrm.domain.company.DTO.CompanyResponse;
import com.example.marinecrm.domain.company.DTO.CompanyUpdateRequest;
import com.example.marinecrm.domain.company.service.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {

    private final CreateCompanyService createCompanyService;
    private final DeleteCompanyService deleteCompanyService;
    private final GetAllCompanyService getAllCompanyService;
    private final GetCompanyByIdService getCompanyByIdService;
    private final UpdateCompanyService updateCompanyService;

    @PostMapping()
    public ResponseEntity<CompanyResponse> createCompany(@Validated @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(createCompanyService.execute(request));
    }

    @GetMapping()
    public ResponseEntity<Page<CompanyResponse>> getCompanies(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(getAllCompanyService.execute(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deleteCompanyService.execute(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable UUID id) {
        return ResponseEntity.ok(getCompanyByIdService.execute(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(@PathVariable UUID id, @Validated @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(updateCompanyService.execute(new CompanyUpdateRequest(id, request)));
    }
}
