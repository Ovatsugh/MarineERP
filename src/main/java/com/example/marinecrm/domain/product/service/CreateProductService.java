package com.example.marinecrm.domain.product.service;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.product.Product;
import com.example.marinecrm.domain.product.ProductRepository;
import com.example.marinecrm.domain.product.DTO.ProductRequest;
import com.example.marinecrm.domain.product.DTO.ProductResponse;
import com.example.marinecrm.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateProductService implements Command<ProductRequest, ProductResponse> {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponse execute(ProductRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Product product = new Product(request, user);

        Product saved = productRepository.save(product);

        return new ProductResponse(saved);
    }
}
