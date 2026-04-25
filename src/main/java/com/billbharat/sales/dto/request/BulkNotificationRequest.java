package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BulkNotificationRequest {

    @NotEmpty(message = "Recipient IDs are required")
    private List<String> recipientIds;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    private Map<String, Object> data;
}
