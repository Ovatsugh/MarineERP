package com.example.marinecrm.domain.product;

import com.example.marinecrm.domain.company.Company;
import com.example.marinecrm.domain.product.DTO.ProductRequest;
import com.example.marinecrm.domain.product.DTO.ProductUpdateRequest;
import com.example.marinecrm.domain.sale.ItemSale;
import com.example.marinecrm.domain.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<ItemSale> itemSales;

    @Column(name = "bike_model")
    private String bikeModel;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Integer stock_quantity;

    @Column(name = "description")
    private String description;

    public Product(ProductRequest request, User user) {
        this.name = request.name();
        this.user = user;
        this.price = request.price();
        this.stock_quantity = request.stock_quantity();
        this.bikeModel = request.bikeModel();
        this.code = request.code();
        this.description = request.description();
    }

    public void update(ProductUpdateRequest request) {
        this.name = request.payload().name();
        this.price = request.payload().price();
        this.stock_quantity = request.payload().stock_quantity();
        this.bikeModel = request.payload().bikeModel();
        this.code = request.payload().code();
        this.description = request.payload().description();
    }
}
