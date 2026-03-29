package com.example.marinecrm.domain.company;

import com.example.marinecrm.domain.company.DTO.CompanyRequest;
import com.example.marinecrm.domain.company.DTO.CompanyUpdateRequest;
import com.example.marinecrm.domain.product.Product;
import com.example.marinecrm.domain.sale.Sale;
import com.example.marinecrm.domain.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "companies")
public class Company {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "cnpj")
    private String cnpj;

    @OneToMany(mappedBy = "company")
    @JsonManagedReference
    private List<User> users;

    @OneToMany(mappedBy = "company")
    @JsonManagedReference
    private List<Product> products;

    @OneToMany(mappedBy = "company")
    @JsonManagedReference
    private List<Sale> sales;

    public Company(CompanyRequest request) {
        this.name = request.name();
        this.cnpj = request.cnpj();
    }

    public void update(CompanyUpdateRequest request) {
        this.name = request.payload().name();
        this.cnpj = request.payload().cnpj();
    }
}
