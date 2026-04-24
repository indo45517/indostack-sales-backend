package com.billbharat.sales.dto.response;

import com.billbharat.sales.entity.Territory;
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
public class TerritoryResponse {
    private String id;
    private String name;
    private String description;
    private String assignedTo;
    private BigDecimal centerLatitude;
    private BigDecimal centerLongitude;
    private Double radiusKm;
    private boolean isActive;
    private LocalDateTime createdAt;

    public static TerritoryResponse fromEntity(Territory territory) {
        return TerritoryResponse.builder()
                .id(territory.getId().toString())
                .name(territory.getName())
                .description(territory.getDescription())
                .assignedTo(territory.getAssignedTo() != null ? territory.getAssignedTo().toString() : null)
                .centerLatitude(territory.getCenterLatitude())
                .centerLongitude(territory.getCenterLongitude())
                .radiusKm(territory.getRadiusKm())
                .isActive(territory.isActive())
                .createdAt(territory.getCreatedAt())
                .build();
    }
}
