package com.billbharat.sales.controller;

import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.UnauthorizedException;
import com.billbharat.sales.repository.MerchantRepository;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Map", description = "Map and location endpoints")
@SecurityRequirement(name = "bearerAuth")
public class MapController {

    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping("/merchants/map")
    @Operation(summary = "Get merchant map markers")
    public ResponseEntity<Map<String, Object>> getMerchantMarkers(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(defaultValue = "10") double radius) {
        List<Map<String, Object>> markers;
        if (lat != null && lng != null) {
            markers = merchantRepository.findNearbyMerchants(BigDecimal.valueOf(lat), BigDecimal.valueOf(lng), radius)
                    .stream()
                    .map(m -> {
                        Map<String, Object> marker = new HashMap<>();
                        marker.put("id", m.getId().toString());
                        marker.put("shopName", m.getShopName());
                        marker.put("latitude", m.getLatitude());
                        marker.put("longitude", m.getLongitude());
                        return marker;
                    })
                    .toList();
        } else {
            markers = merchantRepository.findByIsActiveTrueOrderByShopNameAsc()
                    .stream()
                    .map(m -> {
                        Map<String, Object> marker = new HashMap<>();
                        marker.put("id", m.getId().toString());
                        marker.put("shopName", m.getShopName());
                        marker.put("latitude", m.getLatitude());
                        marker.put("longitude", m.getLongitude());
                        return marker;
                    })
                    .toList();
        }
        return ResponseEntity.ok(ResponseUtil.success("Merchant markers", markers));
    }

    @PostMapping("/route/optimize")
    @Operation(summary = "Optimize route for deliveries")
    public ResponseEntity<Map<String, Object>> optimizeRoute(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        result.put("optimizedRoute", List.of());
        result.put("totalDistance", 0);
        return ResponseEntity.ok(ResponseUtil.success("Route optimized", result));
    }
}
