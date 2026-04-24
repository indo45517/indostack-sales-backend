package com.billbharat.sales.controller;

import com.billbharat.sales.dto.request.PaperStockRequest;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.UnauthorizedException;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.DeliveryService;
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
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Deliveries", description = "Paper roll delivery management")
@SecurityRequirement(name = "bearerAuth")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping("/deliveries")
    @Operation(summary = "Get deliveries (paginated)")
    public ResponseEntity<Map<String, Object>> getDeliveries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = deliveryService.getDeliveries(getCurrentUser().getId(),
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ResponseUtil.paginated(result.getContent(), result.getTotalElements(), page + 1, size));
    }

    @PostMapping("/deliveries/{id}/deliver")
    @Operation(summary = "Mark delivery as delivered")
    public ResponseEntity<Map<String, Object>> markDelivered(@PathVariable UUID id) {
        var response = deliveryService.markDelivered(id, getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Delivery marked as delivered", response));
    }

    @PostMapping("/inventory/paper-request")
    @Operation(summary = "Request paper stock")
    public ResponseEntity<Map<String, Object>> requestPaperStock(@Valid @RequestBody PaperStockRequest request) {
        var result = deliveryService.requestPaperStock(getCurrentUser().getId(), request);
        return ResponseEntity.ok(ResponseUtil.success("Paper stock requested", result));
    }
}
