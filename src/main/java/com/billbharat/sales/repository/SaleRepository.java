package com.billbharat.sales.repository;

import com.billbharat.sales.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {
    Page<Sale> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    long countByUserIdAndCreatedAtBetween(UUID userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.finalAmount), 0) FROM Sale s WHERE s.userId = :userId AND s.createdAt BETWEEN :start AND :end AND s.status = 'COMPLETED'")
    BigDecimal sumFinalAmountByUserIdAndDateRange(@Param("userId") UUID userId,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    @Query("SELECT s FROM Sale s WHERE s.userId = :userId AND s.createdAt BETWEEN :start AND :end ORDER BY s.createdAt DESC")
    Page<Sale> findByUserIdAndDateRange(@Param("userId") UUID userId,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        Pageable pageable);

    @Query("SELECT s.userId, SUM(s.finalAmount) as totalSales FROM Sale s WHERE s.createdAt BETWEEN :start AND :end AND s.status = 'COMPLETED' GROUP BY s.userId ORDER BY totalSales DESC")
    java.util.List<Object[]> findLeaderboardByDateRange(@Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end,
                                                        Pageable pageable);

    @Query("SELECT COALESCE(SUM(s.finalAmount), 0) FROM Sale s WHERE s.status = 'COMPLETED'")
    BigDecimal sumAllFinalAmounts();

    @Query("SELECT COALESCE(SUM(s.finalAmount), 0) FROM Sale s WHERE s.userId IN :userIds AND s.createdAt BETWEEN :start AND :end AND s.status = 'COMPLETED'")
    BigDecimal sumFinalAmountByUserIdsAndDateRange(@Param("userIds") List<UUID> userIds,
                                                   @Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.userId IN :userIds AND s.createdAt BETWEEN :start AND :end")
    long countByUserIdsAndCreatedAtBetween(@Param("userIds") List<UUID> userIds,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}
