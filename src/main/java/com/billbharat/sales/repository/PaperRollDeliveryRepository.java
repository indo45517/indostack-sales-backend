package com.billbharat.sales.repository;

import com.billbharat.sales.entity.PaperRollDelivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaperRollDeliveryRepository extends JpaRepository<PaperRollDelivery, UUID> {
    Page<PaperRollDelivery> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Page<PaperRollDelivery> findByStatusOrderByCreatedAtDesc(PaperRollDelivery.Status status, Pageable pageable);
    long countByUserIdAndStatus(UUID userId, PaperRollDelivery.Status status);
}
