package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.AttendanceRequest;
import com.billbharat.sales.dto.response.AttendanceResponse;
import com.billbharat.sales.entity.AttendanceRecord;
import com.billbharat.sales.exception.BadRequestException;
import com.billbharat.sales.repository.AttendanceRepository;
import com.billbharat.sales.service.AttendanceService;
import com.billbharat.sales.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Override
    @Transactional
    public AttendanceResponse checkIn(UUID userId, AttendanceRequest request) {
        LocalDate today = DateUtil.today();
        attendanceRepository.findByUserIdAndAttendanceDate(userId, today)
                .ifPresent(r -> { throw new BadRequestException("Already checked in today"); });

        AttendanceRecord record = AttendanceRecord.builder()
                .userId(userId)
                .checkInLatitude(request.getLatitude())
                .checkInLongitude(request.getLongitude())
                .checkInTime(DateUtil.now())
                .attendanceDate(today)
                .status(AttendanceRecord.Status.CHECKED_IN)
                .build();

        return AttendanceResponse.fromEntity(attendanceRepository.save(record));
    }

    @Override
    @Transactional
    public AttendanceResponse checkOut(UUID userId, AttendanceRequest request) {
        LocalDate today = DateUtil.today();
        AttendanceRecord record = attendanceRepository.findByUserIdAndAttendanceDate(userId, today)
                .orElseThrow(() -> new BadRequestException("No check-in found for today"));

        if (record.getStatus() == AttendanceRecord.Status.CHECKED_OUT) {
            throw new BadRequestException("Already checked out today");
        }

        record.setCheckOutLatitude(request.getLatitude());
        record.setCheckOutLongitude(request.getLongitude());
        record.setCheckOutTime(DateUtil.now());
        record.setStatus(AttendanceRecord.Status.CHECKED_OUT);

        return AttendanceResponse.fromEntity(attendanceRepository.save(record));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getAttendanceHistory(UUID userId, Pageable pageable) {
        return attendanceRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(AttendanceResponse::fromEntity);
    }
}
