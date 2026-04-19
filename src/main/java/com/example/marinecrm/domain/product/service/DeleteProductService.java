package com.example.marinecrm.domain.product.service;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.product.Product;
import com.example.marinecrm.domain.product.ProductRepository;
import com.example.marinecrm.domain.sale.ItemSaleRepository;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.exceptions.ForbiddenException;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteProductService implements Command<UUID, Void> {

    private final ProductRepository productRepository;
    private final ItemSaleRepository itemSaleRepository;

    @Override
    @Transactional
    public Void execute(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Produto não encontrado: " + id));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!product.getUser().getCompany().getId().equals(user.getCompany().getId())) {
            throw new ForbiddenException();
        }

        itemSaleRepository.findByProduct_Id(id).forEach(item -> item.setProduct(null));

        productRepository.delete(product);
        return null;
    }
}
