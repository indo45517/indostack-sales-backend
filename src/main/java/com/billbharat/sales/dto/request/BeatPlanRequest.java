package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BeatPlanRequest {

    @NotBlank(message = "Beat name is required")
    private String beatName;

    @NotBlank(message = "Date is required")
    private String date;

    private String territoryId;

    @NotBlank(message = "Executive ID is required")
    private String executiveId;

    private Integer visitTarget;

    private BigDecimal salesTarget;
}
