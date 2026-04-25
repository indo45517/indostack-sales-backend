package com.billbharat.sales.controller;

import com.billbharat.sales.dto.request.CreateProductRequest;
import com.billbharat.sales.service.ProductService;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    @Operation(summary = "Get all active products")
    public ResponseEntity<Map<String, Object>> getProducts() {
        var products = productService.getActiveProducts();
        return ResponseEntity.ok(ResponseUtil.success("Products retrieved", products));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable UUID id) {
        var product = productService.getProductById(id);
        return ResponseEntity.ok(ResponseUtil.success("Product retrieved", product));
    }

    @PostMapping("/admin/products")
    @Operation(summary = "Create a new product (Admin only)")
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        var product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success("Product created", product));
    }

    @PutMapping("/admin/products/{id}")
    @Operation(summary = "Update a product (Admin only)")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProductRequest request) {
        var product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ResponseUtil.success("Product updated", product));
    }

    @DeleteMapping("/admin/products/{id}")
    @Operation(summary = "Soft delete a product (Admin only)")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResponseUtil.success("Product deleted", null));
    }
}
