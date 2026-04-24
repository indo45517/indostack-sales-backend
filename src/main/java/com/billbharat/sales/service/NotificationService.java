package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.NotificationRegisterRequest;
import com.billbharat.sales.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface NotificationService {
    Map<String, Object> registerDevice(UUID userId, NotificationRegisterRequest request);
    Page<NotificationResponse> getNotifications(UUID userId, Pageable pageable);
    NotificationResponse markAsRead(UUID notificationId, UUID userId);
    void markAllAsRead(UUID userId);
    void deleteNotification(UUID notificationId, UUID userId);
}
