package com.example.marinecrm.domain.product.service;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.product.Product;
import com.example.marinecrm.domain.product.ProductRepository;
import com.example.marinecrm.domain.product.DTO.ProductResponse;
import com.example.marinecrm.domain.product.DTO.ProductUpdateRequest;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.exceptions.ForbiddenException;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProductService implements Command<ProductUpdateRequest, ProductResponse> {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponse execute(ProductUpdateRequest request) {
        Product product = productRepository.findById(request.id()).orElseThrow(() ->
                new ResourceNotFoundException("Produto não encontrado: " + request.id()));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!product.getUser().getCompany().getId().equals(user.getCompany().getId())) {
            throw new ForbiddenException();
        }

        product.update(request);

        Product saved = productRepository.save(product);

        return new ProductResponse(saved);
    }
}
