package org.yascode.inventory_saas.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yascode.inventory_saas.common.PageResponse;
import org.yascode.inventory_saas.requests.ProductRequest;
import org.yascode.inventory_saas.responses.ProductResponse;
import org.yascode.inventory_saas.services.ProductService;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product API")
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<Void> createProduct(
            @RequestBody
            @Valid
            final ProductRequest request
    ) {
        this.service.create(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{product-id}")
    public ResponseEntity<Void> updateProduct(
            @RequestBody
            @Valid
            final ProductRequest request,
            @PathVariable("product-id")
            @NotNull(message = "Product ID cannot be null")
            final String id
    ) {
        this.service.update(id, request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{product-id}")
    public ResponseEntity<ProductResponse> findProductById(
            @PathVariable("product-id")
            @NotNull(message = "Product ID cannot be null")
            final String id
    ) {
        return ResponseEntity.ok(this.service.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> findAllProducts(
            @RequestParam(name = "page", defaultValue = "0")
            final int page,
            @RequestParam(name = "size", defaultValue = "10")
            final int size
    )  {
        return ResponseEntity.ok(this.service.findAll(page, size));
    }

    @DeleteMapping("/{product-id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("product-id")
            @NotNull(message = "Product ID cannot be null")
            final String id
    ) {
        this.service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
