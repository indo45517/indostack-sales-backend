package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "MRP is required")
    @DecimalMin(value = "0.0", message = "MRP must be non-negative")
    private BigDecimal mrp;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.0", message = "Selling price must be non-negative")
    private BigDecimal sellingPrice;

    @NotNull(message = "hasCommission is required")
    private Boolean hasCommission;

    private BigDecimal commissionAmount;

    private String imageUrl;
}
