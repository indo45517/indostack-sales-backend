package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TerritoryRemoveRequest {
    @NotBlank(message = "Executive ID is required")
    private String executiveId;
}
