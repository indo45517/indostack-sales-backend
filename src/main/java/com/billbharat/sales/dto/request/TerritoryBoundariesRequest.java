package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TerritoryBoundariesRequest {

    @NotEmpty(message = "Boundaries are required")
    private List<Map<String, Double>> boundaries;
}
