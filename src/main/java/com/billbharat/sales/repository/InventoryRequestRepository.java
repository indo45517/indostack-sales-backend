package com.billbharat.sales.repository;

import com.billbharat.sales.entity.InventoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InventoryRequestRepository extends JpaRepository<InventoryRequest, UUID> {
    Page<InventoryRequest> findByRequestedByOrderByCreatedAtDesc(UUID requestedBy, Pageable pageable);
    Page<InventoryRequest> findByStatusOrderByCreatedAtDesc(InventoryRequest.Status status, Pageable pageable);
}
