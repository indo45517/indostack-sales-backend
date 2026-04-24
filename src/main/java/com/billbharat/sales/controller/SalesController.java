package com.billbharat.sales.controller;

import com.billbharat.sales.dto.request.SaleRequest;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.UnauthorizedException;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.SalesService;
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
@Tag(name = "Sales", description = "Sales management")
@SecurityRequirement(name = "bearerAuth")
public class SalesController {

    private final SalesService salesService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @PostMapping("/sales")
    @Operation(summary = "Create a new sale")
    public ResponseEntity<Map<String, Object>> createSale(@Valid @RequestBody SaleRequest request) {
        var response = salesService.createSale(getCurrentUser().getId(), request);
        return ResponseEntity.ok(ResponseUtil.success("Sale created successfully", response));
    }

    @GetMapping("/sales")
    @Operation(summary = "Get sales (paginated)")
    public ResponseEntity<Map<String, Object>> getSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = salesService.getSales(getCurrentUser().getId(),
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ResponseUtil.paginated(result.getContent(), result.getTotalElements(), page + 1, size));
    }

    @GetMapping("/sales/{id}")
    @Operation(summary = "Get sale by ID")
    public ResponseEntity<Map<String, Object>> getSaleById(@PathVariable UUID id) {
        var response = salesService.getSaleById(id);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @GetMapping("/coupons/validate")
    @Operation(summary = "Validate a coupon code")
    public ResponseEntity<Map<String, Object>> validateCoupon(@RequestParam String code) {
        var result = salesService.validateCoupon(code);
        return ResponseEntity.ok(ResponseUtil.success("Coupon is valid", result));
    }
}
