package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.NotificationRegisterRequest;
import com.billbharat.sales.dto.response.NotificationResponse;
import com.billbharat.sales.entity.Notification;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.BadRequestException;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.NotificationRepository;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.NotificationService;
import com.billbharat.sales.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Map<String, Object> registerDevice(UUID userId, NotificationRegisterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setDeviceToken(request.getDeviceToken());
        userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("registered", true);
        result.put("deviceToken", request.getDeviceToken());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationResponse::fromEntity);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new BadRequestException("Not authorized");
        }

        notification.setRead(true);
        notification.setReadAt(DateUtil.now());
        return NotificationResponse.fromEntity(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        if (!notification.getUserId().equals(userId)) {
            throw new BadRequestException("Not authorized");
        }
        notificationRepository.delete(notification);
    }
}
