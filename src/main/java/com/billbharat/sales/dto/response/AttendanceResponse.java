package com.billbharat.sales.dto.response;

import com.billbharat.sales.entity.AttendanceRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private String id;
    private String userId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private BigDecimal checkInLatitude;
    private BigDecimal checkInLongitude;
    private BigDecimal checkOutLatitude;
    private BigDecimal checkOutLongitude;
    private String status;
    private String attendanceDate;
    private LocalDateTime createdAt;

    public static AttendanceResponse fromEntity(AttendanceRecord record) {
        return AttendanceResponse.builder()
                .id(record.getId().toString())
                .userId(record.getUserId().toString())
                .checkInTime(record.getCheckInTime())
                .checkOutTime(record.getCheckOutTime())
                .checkInLatitude(record.getCheckInLatitude())
                .checkInLongitude(record.getCheckInLongitude())
                .checkOutLatitude(record.getCheckOutLatitude())
                .checkOutLongitude(record.getCheckOutLongitude())
                .status(record.getStatus().name())
                .attendanceDate(record.getAttendanceDate() != null ? record.getAttendanceDate().toString() : null)
                .createdAt(record.getCreatedAt())
                .build();
    }
}
