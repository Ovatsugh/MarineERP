package com.example.marinecrm.domain.sale.DTO;

import com.example.marinecrm.domain.customer.DTO.CustomerResponse;
import com.example.marinecrm.domain.sale.Sale;

import java.math.BigDecimal;
import java.util.UUID;

public record SalesResponse(UUID id, CustomerResponse customer, BigDecimal amount, String notes) {
    public SalesResponse(Sale sales) {
        this(sales.getId(), new CustomerResponse(sales.getCustomer()), sales.getAmount(), sales.getNotes());
    }
}
