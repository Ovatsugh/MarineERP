package com.example.marinecrm.infra.bootstrap;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.company.CompanyRepository;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.UserRepository;
import com.example.marinecrm.enums.Roles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitialDataSeeder implements ApplicationRunner {

    private static final String INITIAL_ADMIN_EMAIL = "gus@mail.com";

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (companyRepository.count() > 0 || userRepository.count() > 0) {
            return;
        }

        Company company = new Company();
        company.setName("Empresa Ficticia Marine");
        company.setCnpj("12345678000190");

        Company savedCompany = companyRepository.save(company);

        User admin = new User();
        admin.setName("Gus Admin");
        admin.setEmail(INITIAL_ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode("1234"));
        admin.setRole(Roles.ADMIN);
        admin.setCompany(savedCompany);

        userRepository.save(admin);

        log.info("Seed inicial criado com empresa ficticia e admin {}", INITIAL_ADMIN_EMAIL);
    }
}
