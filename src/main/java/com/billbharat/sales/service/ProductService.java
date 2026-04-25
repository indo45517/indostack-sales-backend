package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.CreateProductRequest;
import com.billbharat.sales.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductResponse> getActiveProducts();
    ProductResponse getProductById(UUID id);
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse updateProduct(UUID id, CreateProductRequest request);
    void deleteProduct(UUID id);
}
