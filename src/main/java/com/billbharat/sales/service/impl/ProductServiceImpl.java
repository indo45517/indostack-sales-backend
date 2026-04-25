package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.CreateProductRequest;
import com.billbharat.sales.dto.response.ProductResponse;
import com.billbharat.sales.entity.Product;
import com.billbharat.sales.exception.BadRequestException;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.ProductRepository;
import com.billbharat.sales.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return ProductResponse.fromEntity(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product.Category category = parseCategory(request.getCategory());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .mrp(request.getMrp())
                .sellingPrice(request.getSellingPrice())
                .hasCommission(Boolean.TRUE.equals(request.getHasCommission()))
                .commissionAmount(request.getCommissionAmount())
                .imageUrl(request.getImageUrl())
                .isActive(true)
                .build();

        product = productRepository.save(product);
        log.info("Created product: {} ({})", product.getName(), product.getId());
        return ProductResponse.fromEntity(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, CreateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Product.Category category = parseCategory(request.getCategory());

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setMrp(request.getMrp());
        product.setSellingPrice(request.getSellingPrice());
        product.setHasCommission(Boolean.TRUE.equals(request.getHasCommission()));
        product.setCommissionAmount(request.getCommissionAmount());
        product.setImageUrl(request.getImageUrl());

        product = productRepository.save(product);
        log.info("Updated product: {} ({})", product.getName(), product.getId());
        return ProductResponse.fromEntity(product);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setActive(false);
        productRepository.save(product);
        log.info("Soft-deleted product: {} ({})", product.getName(), product.getId());
    }

    private Product.Category parseCategory(String category) {
        try {
            return Product.Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid category: " + category +
                    ". Valid values are: POS, SOFTWARE, MERCHANDISE, SERVICE, OTHER");
        }
    }
}
