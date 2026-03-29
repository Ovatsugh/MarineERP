package com.example.marinecrm.domain.sale.services;

import com.example.marinecrm.Command;
import com.example.marinecrm.domain.customer.Customer;
import com.example.marinecrm.domain.customer.CustomerRepository;
import com.example.marinecrm.domain.product.Product;
import com.example.marinecrm.domain.product.ProductRepository;
import com.example.marinecrm.domain.sale.ItemSale;
import com.example.marinecrm.domain.sale.ItemSaleRepository;
import com.example.marinecrm.domain.sale.Sale;
import com.example.marinecrm.domain.sale.SaleRepository;
import com.example.marinecrm.domain.sale.DTO.SalesRequest;
import com.example.marinecrm.domain.sale.DTO.SalesResponse;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.exceptions.InsufficientStockException;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateSaleService implements Command<SalesRequest, SalesResponse> {

    private final SaleRepository saleRepository;
    private final ItemSaleRepository itemSaleRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public SalesResponse execute(SalesRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Customer customer = customerRepository.findById(request.customerId()).orElseThrow(() ->
                new ResourceNotFoundException("Cliente não encontrado: " + request.customerId()));

        List<ItemSale> items = request.items().stream().map(itemRequest -> {
            Product product = productRepository.findById(itemRequest.productId()).orElseThrow(() ->
                    new ResourceNotFoundException("Produto não encontrado: " + itemRequest.productId()));

            if (product.getStock_quantity() < itemRequest.quantity()) {
                throw new InsufficientStockException(product.getName(), product.getStock_quantity());
            }

            product.setStock_quantity(product.getStock_quantity() - itemRequest.quantity());
            productRepository.save(product);

            return new ItemSale(itemRequest, null, product);
        }).toList();

        BigDecimal totalAmount = items.stream()
                .map(ItemSale::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Sale sale = new Sale(request, user, customer, totalAmount);
        Sale saved = saleRepository.save(sale);

        items.forEach(item -> {
            item.setSale(saved);
            itemSaleRepository.save(item);
        });

        return new SalesResponse(saved);
    }
}
