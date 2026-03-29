package com.example.marinecrm.domain.product;

import com.example.marinecrm.domain.product.DTO.ProductRequest;
import com.example.marinecrm.domain.product.DTO.ProductResponse;
import com.example.marinecrm.domain.product.DTO.ProductUpdateRequest;
import com.example.marinecrm.domain.product.service.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final CreateProductService createProductService;
    private final DeleteProductService deleteProductService;
    private final GetAllProductService getAllProductService;
    private final GetProductByIdService getProductByIdService;
    private final UpdateProductService updateProductService;

    @PostMapping()
    public ResponseEntity<ProductResponse> createProduct(@Validated @RequestBody ProductRequest request) {
        return ResponseEntity.ok(createProductService.execute(request));
    }

    @GetMapping()
    public ResponseEntity<Page<ProductResponse>> getProducts(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(getAllProductService.execute(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deleteProductService.execute(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(getProductByIdService.execute(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID id, @Validated @RequestBody ProductRequest request) {
        return ResponseEntity.ok(updateProductService.execute(new ProductUpdateRequest(id, request)));
    }
}
