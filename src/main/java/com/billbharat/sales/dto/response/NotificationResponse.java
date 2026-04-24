package com.billbharat.sales.dto.response;

import com.billbharat.sales.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private String id;
    private String userId;
    private String title;
    private String body;
    private String type;
    private String referenceId;
    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    public static NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId().toString())
                .userId(notification.getUserId().toString())
                .title(notification.getTitle())
                .body(notification.getBody())
                .type(notification.getType().name())
                .referenceId(notification.getReferenceId() != null ? notification.getReferenceId().toString() : null)
                .isRead(notification.isRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
