package com.billbharat.sales.controller;

import com.billbharat.sales.dto.request.NotificationRegisterRequest;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.UnauthorizedException;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.NotificationService;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Push notification management")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @PostMapping("/register")
    @Operation(summary = "Register device for push notifications")
    public ResponseEntity<Map<String, Object>> registerDevice(@Valid @RequestBody NotificationRegisterRequest request) {
        var result = notificationService.registerDevice(getCurrentUser().getId(), request);
        return ResponseEntity.ok(ResponseUtil.success("Device registered", result));
    }

    @GetMapping
    @Operation(summary = "Get notifications")
    public ResponseEntity<Map<String, Object>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = notificationService.getNotifications(getCurrentUser().getId(),
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ResponseUtil.paginated(result.getContent(), result.getTotalElements(), page + 1, size));
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable UUID id) {
        var response = notificationService.markAsRead(id, getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Notification marked as read", response));
    }

    @PostMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        notificationService.markAllAsRead(getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("All notifications marked as read", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id, getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Notification deleted", null));
    }
}
