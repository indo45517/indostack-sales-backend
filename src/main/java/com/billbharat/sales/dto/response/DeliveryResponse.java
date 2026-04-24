package com.billbharat.sales.dto.response;

import com.billbharat.sales.entity.PaperRollDelivery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {
    private String id;
    private String userId;
    private String merchantId;
    private String merchantName;
    private Integer quantity;
    private String status;
    private LocalDateTime assignedTime;
    private LocalDateTime deliveredTime;
    private String deliveryPhotoUrl;
    private String notes;
    private LocalDateTime createdAt;

    public static DeliveryResponse fromEntity(PaperRollDelivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId().toString())
                .userId(delivery.getUserId().toString())
                .merchantId(delivery.getMerchantId().toString())
                .merchantName(delivery.getMerchantName())
                .quantity(delivery.getQuantity())
                .status(delivery.getStatus().name())
                .assignedTime(delivery.getAssignedTime())
                .deliveredTime(delivery.getDeliveredTime())
                .deliveryPhotoUrl(delivery.getDeliveryPhotoUrl())
                .notes(delivery.getNotes())
                .createdAt(delivery.getCreatedAt())
                .build();
    }
}
