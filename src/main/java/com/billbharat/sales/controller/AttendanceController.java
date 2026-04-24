package com.billbharat.sales.controller;

import com.billbharat.sales.dto.request.AttendanceRequest;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.AttendanceService;
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

@RestController
@RequestMapping("/api/v1/sales/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance management")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new com.billbharat.sales.exception.UnauthorizedException("User not found"));
    }

    @PostMapping("/check-in")
    @Operation(summary = "Check in with GPS coordinates")
    public ResponseEntity<Map<String, Object>> checkIn(@Valid @RequestBody AttendanceRequest request) {
        var response = attendanceService.checkIn(getCurrentUser().getId(), request);
        return ResponseEntity.ok(ResponseUtil.success("Checked in successfully", response));
    }

    @PostMapping("/check-out")
    @Operation(summary = "Check out with GPS coordinates")
    public ResponseEntity<Map<String, Object>> checkOut(@Valid @RequestBody AttendanceRequest request) {
        var response = attendanceService.checkOut(getCurrentUser().getId(), request);
        return ResponseEntity.ok(ResponseUtil.success("Checked out successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get attendance history")
    public ResponseEntity<Map<String, Object>> getAttendance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = attendanceService.getAttendanceHistory(getCurrentUser().getId(),
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ResponseUtil.paginated(result.getContent(), result.getTotalElements(), page + 1, size));
    }
}
