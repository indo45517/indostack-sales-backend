package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TerritoryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
    private String assignedTo;
    private Double centerLatitude;
    private Double centerLongitude;
    private Double radiusKm;
}
