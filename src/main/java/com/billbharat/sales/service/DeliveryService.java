package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.PaperStockRequest;
import com.billbharat.sales.dto.response.DeliveryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface DeliveryService {
    Page<DeliveryResponse> getDeliveries(UUID userId, Pageable pageable);
    DeliveryResponse markDelivered(UUID deliveryId, UUID userId);
    Map<String, Object> requestPaperStock(UUID userId, PaperStockRequest request);
}
