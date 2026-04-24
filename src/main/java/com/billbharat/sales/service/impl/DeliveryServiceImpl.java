package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.PaperStockRequest;
import com.billbharat.sales.dto.response.DeliveryResponse;
import com.billbharat.sales.entity.InventoryRequest;
import com.billbharat.sales.entity.PaperRollDelivery;
import com.billbharat.sales.exception.BadRequestException;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.InventoryRequestRepository;
import com.billbharat.sales.repository.PaperRollDeliveryRepository;
import com.billbharat.sales.service.DeliveryService;
import com.billbharat.sales.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final PaperRollDeliveryRepository deliveryRepository;
    private final InventoryRequestRepository inventoryRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<DeliveryResponse> getDeliveries(UUID userId, Pageable pageable) {
        return deliveryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(DeliveryResponse::fromEntity);
    }

    @Override
    @Transactional
    public DeliveryResponse markDelivered(UUID deliveryId, UUID userId) {
        PaperRollDelivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery", "id", deliveryId));

        if (!delivery.getUserId().equals(userId)) {
            throw new BadRequestException("Not authorized to update this delivery");
        }
        if (delivery.getStatus() == PaperRollDelivery.Status.DELIVERED) {
            throw new BadRequestException("Delivery already marked as delivered");
        }

        delivery.setStatus(PaperRollDelivery.Status.DELIVERED);
        delivery.setDeliveredTime(DateUtil.now());

        return DeliveryResponse.fromEntity(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional
    public Map<String, Object> requestPaperStock(UUID userId, PaperStockRequest request) {
        InventoryRequest inventoryRequest = InventoryRequest.builder()
                .requestedBy(userId)
                .inventoryItemId(UUID.randomUUID()) // default item
                .quantityRequested(request.getQuantity())
                .notes(request.getNotes())
                .status(InventoryRequest.Status.PENDING)
                .build();

        InventoryRequest saved = inventoryRequestRepository.save(inventoryRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("requestId", saved.getId().toString());
        result.put("quantityRequested", saved.getQuantityRequested());
        result.put("status", saved.getStatus().name());
        result.put("message", "Paper stock request submitted successfully");
        return result;
    }
}
