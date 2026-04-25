package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReassignExecutiveRequest {

    @NotNull(message = "New lead ID is required")
    private UUID newLeadId;
}
