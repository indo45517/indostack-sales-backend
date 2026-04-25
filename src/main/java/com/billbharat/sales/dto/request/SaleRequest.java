package com.billbharat.sales.dto.request;

import com.billbharat.sales.entity.Sale;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String productId;
    private String merchantId;
    private String couponCode;

    private Sale.PaymentMethod paymentMethod = Sale.PaymentMethod.CASH;
    private String productDetails;
    private String notes;
}
