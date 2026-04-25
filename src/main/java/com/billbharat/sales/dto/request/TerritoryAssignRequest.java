package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TerritoryAssignRequest {

    @NotBlank(message = "Executive ID is required")
    private String executiveId;

    private String startDate;

    private String endDate;
}
