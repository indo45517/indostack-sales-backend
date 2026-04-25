package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DeliveryAssignRequest {

    @NotBlank(message = "Executive ID is required")
    private String executiveId;

    @NotEmpty(message = "Items are required")
    private List<Map<String, Object>> items;

    private String priority;

    private String deliveryDate;

    private String notes;
}
