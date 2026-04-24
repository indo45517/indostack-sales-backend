package com.billbharat.sales.dto.response;

import com.billbharat.sales.entity.VisitLog;
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
public class VisitResponse {
    private String id;
    private String userId;
    private String merchantId;
    private String shopName;
    private String ownerName;
    private String phoneNumber;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime visitTime;
    private String photoUrl;
    private String notes;
    private String status;
    private String purpose;
    private LocalDateTime createdAt;

    public static VisitResponse fromEntity(VisitLog visitLog) {
        return VisitResponse.builder()
                .id(visitLog.getId().toString())
                .userId(visitLog.getUserId().toString())
                .merchantId(visitLog.getMerchantId() != null ? visitLog.getMerchantId().toString() : null)
                .shopName(visitLog.getShopName())
                .ownerName(visitLog.getOwnerName())
                .phoneNumber(visitLog.getPhoneNumber())
                .latitude(visitLog.getLatitude())
                .longitude(visitLog.getLongitude())
                .visitTime(visitLog.getVisitTime())
                .photoUrl(visitLog.getPhotoUrl())
                .notes(visitLog.getNotes())
                .status(visitLog.getStatus().name())
                .purpose(visitLog.getPurpose())
                .createdAt(visitLog.getCreatedAt())
                .build();
    }
}
