package com.example.marinecrm.domain.user;

import com.example.marinecrm.domain.auth.DTO.AuthRequest;
import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.customer.Customer;
import com.example.marinecrm.domain.product.Product;
import com.example.marinecrm.domain.sale.Sale;
import com.example.marinecrm.domain.user.DTO.UserRequest;
import com.example.marinecrm.domain.user.DTO.UserUpdateRequest;
import com.example.marinecrm.enums.Roles;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Roles role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Product> products;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Customer> customers;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Sale> sales;

    public User(UserRequest request, String encodedPassword, Company company) {
        this.name = request.name();
        this.email = request.email();
        this.password = encodedPassword;
        this.role = request.role();
        this.company = company;
    }

    public User(AuthRequest request, String encodedPassword, Company company, @NonNull Roles role) {
        this.name = request.name();
        this.email = request.email();
        this.password = encodedPassword;
        this.role = role;
        this.company = company;
    }


    public void update(UserUpdateRequest request, String encodedPassword) {
        this.name = request.payload().name();
        this.email = request.payload().email();
        this.password = encodedPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }
}
