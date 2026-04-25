package com.billbharat.sales.repository;

import com.billbharat.sales.entity.AttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, UUID> {
    Page<AttendanceRecord> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Optional<AttendanceRecord> findByUserIdAndAttendanceDate(UUID userId, LocalDate date);
    long countByUserIdAndAttendanceDateBetween(UUID userId, LocalDate start, LocalDate end);

    @Query("SELECT COUNT(DISTINCT a.userId) FROM AttendanceRecord a WHERE a.userId IN :userIds AND a.attendanceDate = :date")
    long countDistinctUserIdByUserIdInAndAttendanceDate(@Param("userIds") List<UUID> userIds,
                                                        @Param("date") LocalDate date);
}
