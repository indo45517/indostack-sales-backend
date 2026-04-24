package com.billbharat.sales.repository;

import com.billbharat.sales.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    Page<Merchant> findByAssignedExecutiveIdAndIsActiveTrueOrderByShopNameAsc(UUID executiveId, Pageable pageable);
    List<Merchant> findByIsActiveTrueOrderByShopNameAsc();

    @Query("SELECT m FROM Merchant m WHERE m.isActive = true AND " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(m.latitude)) * cos(radians(m.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(m.latitude)))) < :radiusKm")
    List<Merchant> findNearbyMerchants(@Param("lat") BigDecimal latitude,
                                       @Param("lng") BigDecimal longitude,
                                       @Param("radiusKm") double radiusKm);
}
