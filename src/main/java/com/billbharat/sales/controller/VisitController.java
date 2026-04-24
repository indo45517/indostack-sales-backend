package com.billbharat.sales.controller;

import com.billbharat.sales.dto.request.VisitRequest;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.UnauthorizedException;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.VisitService;
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
@RequestMapping("/api/v1/sales/visits")
@RequiredArgsConstructor
@Tag(name = "Visits", description = "Shop visit management")
@SecurityRequirement(name = "bearerAuth")
public class VisitController {

    private final VisitService visitService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @PostMapping
    @Operation(summary = "Log a shop visit")
    public ResponseEntity<Map<String, Object>> logVisit(@Valid @RequestBody VisitRequest request) {
        var response = visitService.logVisit(getCurrentUser().getId(), request);
        return ResponseEntity.ok(ResponseUtil.success("Visit logged successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get visit history (paginated)")
    public ResponseEntity<Map<String, Object>> getVisits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = visitService.getVisits(getCurrentUser().getId(),
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ResponseUtil.paginated(result.getContent(), result.getTotalElements(), page + 1, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get visit by ID")
    public ResponseEntity<Map<String, Object>> getVisitById(@PathVariable UUID id) {
        var response = visitService.getVisitById(id);
        return ResponseEntity.ok(ResponseUtil.success(response));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Get nearby visits")
    public ResponseEntity<Map<String, Object>> getNearbyVisits(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radius) {
        return ResponseEntity.ok(ResponseUtil.success("Nearby visits", java.util.List.of()));
    }
}
