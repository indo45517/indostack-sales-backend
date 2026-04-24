package com.billbharat.sales.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Records attendance check-in and check-out events with GPS coordinates.
 */
@Entity
@Table(name = "attendance_records")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "check_in_latitude", precision = 10, scale = 7)
    private BigDecimal checkInLatitude;

    @Column(name = "check_in_longitude", precision = 10, scale = 7)
    private BigDecimal checkInLongitude;

    @Column(name = "check_out_latitude", precision = 10, scale = 7)
    private BigDecimal checkOutLatitude;

    @Column(name = "check_out_longitude", precision = 10, scale = 7)
    private BigDecimal checkOutLongitude;

    @Column(name = "attendance_date")
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.CHECKED_IN;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum Status {
        CHECKED_IN, CHECKED_OUT, ABSENT
    }
}
