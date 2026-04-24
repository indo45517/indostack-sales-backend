package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.SaleRequest;
import com.billbharat.sales.dto.response.SaleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface SalesService {
    SaleResponse createSale(UUID userId, SaleRequest request);
    Page<SaleResponse> getSales(UUID userId, Pageable pageable);
    SaleResponse getSaleById(UUID saleId);
    Map<String, Object> validateCoupon(String couponCode);
}
