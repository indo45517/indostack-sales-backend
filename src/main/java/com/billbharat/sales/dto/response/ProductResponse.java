package com.billbharat.sales.dto.response;

import com.billbharat.sales.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private String category;
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private boolean isActive;
    private boolean hasCommission;
    private BigDecimal commissionAmount;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory().name())
                .mrp(product.getMrp())
                .sellingPrice(product.getSellingPrice())
                .isActive(product.isActive())
                .hasCommission(product.isHasCommission())
                .commissionAmount(product.getCommissionAmount())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
