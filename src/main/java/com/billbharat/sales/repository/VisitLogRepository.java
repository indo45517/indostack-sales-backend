package com.billbharat.sales.repository;

import com.billbharat.sales.entity.VisitLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, UUID> {
    Page<VisitLog> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    long countByUserIdAndCreatedAtBetween(UUID userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT v FROM VisitLog v WHERE v.userId = :userId AND v.visitTime BETWEEN :start AND :end ORDER BY v.visitTime DESC")
    Page<VisitLog> findByUserIdAndDateRange(@Param("userId") UUID userId,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             Pageable pageable);

    @Query("SELECT COUNT(v) FROM VisitLog v WHERE v.userId = :userId AND v.visitTime BETWEEN :start AND :end")
    long countByUserIdAndVisitDate(@Param("userId") UUID userId,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(v) FROM VisitLog v WHERE v.userId IN :userIds AND v.createdAt BETWEEN :start AND :end")
    long countByUserIdsAndCreatedAtBetween(@Param("userIds") List<UUID> userIds,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}
