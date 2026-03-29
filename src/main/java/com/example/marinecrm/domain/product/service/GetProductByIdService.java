package com.example.marinecrm.domain.product.service;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.product.ProductRepository;
import com.example.marinecrm.domain.product.DTO.ProductResponse;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetProductByIdService implements Command<UUID, ProductResponse> {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductResponse execute(UUID id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return productRepository.findByIdAndUser_Company_Id(id, user.getCompany().getId())
                .map(ProductResponse::new)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
    }
}
