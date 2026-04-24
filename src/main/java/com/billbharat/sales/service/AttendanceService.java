package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.AttendanceRequest;
import com.billbharat.sales.dto.response.AttendanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AttendanceService {
    AttendanceResponse checkIn(UUID userId, AttendanceRequest request);
    AttendanceResponse checkOut(UUID userId, AttendanceRequest request);
    Page<AttendanceResponse> getAttendanceHistory(UUID userId, Pageable pageable);
}
