package com.billbharat.sales.dto.response;

import com.billbharat.sales.entity.Sale;
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
public class SaleResponse {
    private String id;
    private String userId;
    private String merchantId;
    private String couponId;
    private String productId;
    private String productName;
    private BigDecimal productPrice;
    private boolean hasCommission;
    private BigDecimal commissionAmount;
    private BigDecimal amount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private LocalDateTime saleTime;
    private String invoiceNumber;
    private String paymentMethod;
    private String status;
    private String productDetails;
    private String notes;
    private LocalDateTime createdAt;

    public static SaleResponse fromEntity(Sale sale) {
        return SaleResponse.builder()
                .id(sale.getId().toString())
                .userId(sale.getUserId().toString())
                .merchantId(sale.getMerchantId() != null ? sale.getMerchantId().toString() : null)
                .couponId(sale.getCouponId() != null ? sale.getCouponId().toString() : null)
                .productId(sale.getProductId() != null ? sale.getProductId().toString() : null)
                .productName(sale.getProductName())
                .productPrice(sale.getProductPrice())
                .hasCommission(sale.isHasCommission())
                .commissionAmount(sale.getCommissionAmount())
                .amount(sale.getAmount())
                .discountAmount(sale.getDiscountAmount())
                .finalAmount(sale.getFinalAmount())
                .saleTime(sale.getSaleTime())
                .invoiceNumber(sale.getInvoiceNumber())
                .paymentMethod(sale.getPaymentMethod().name())
                .status(sale.getStatus().name())
                .productDetails(sale.getProductDetails())
                .notes(sale.getNotes())
                .createdAt(sale.getCreatedAt())
                .build();
    }
}
