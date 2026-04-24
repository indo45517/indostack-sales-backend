package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.TerritoryRequest;
import com.billbharat.sales.dto.response.TerritoryResponse;
import com.billbharat.sales.entity.Territory;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.TerritoryRepository;
import com.billbharat.sales.service.TerritoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TerritoryServiceImpl implements TerritoryService {

    private final TerritoryRepository territoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TerritoryResponse> getTerritories(Pageable pageable) {
        return territoryRepository.findByIsActiveTrueOrderByNameAsc(pageable)
                .map(TerritoryResponse::fromEntity);
    }

    @Override
    @Transactional
    public TerritoryResponse createTerritory(TerritoryRequest request) {
        Territory territory = Territory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .assignedTo(StringUtils.hasText(request.getAssignedTo())
                        ? UUID.fromString(request.getAssignedTo()) : null)
                .centerLatitude(request.getCenterLatitude() != null
                        ? BigDecimal.valueOf(request.getCenterLatitude()) : null)
                .centerLongitude(request.getCenterLongitude() != null
                        ? BigDecimal.valueOf(request.getCenterLongitude()) : null)
                .radiusKm(request.getRadiusKm())
                .build();

        return TerritoryResponse.fromEntity(territoryRepository.save(territory));
    }

    @Override
    @Transactional
    public TerritoryResponse updateTerritory(UUID id, TerritoryRequest request) {
        Territory territory = territoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Territory", "id", id));

        territory.setName(request.getName());
        territory.setDescription(request.getDescription());
        if (StringUtils.hasText(request.getAssignedTo())) {
            territory.setAssignedTo(UUID.fromString(request.getAssignedTo()));
        }
        if (request.getCenterLatitude() != null) {
            territory.setCenterLatitude(BigDecimal.valueOf(request.getCenterLatitude()));
        }
        if (request.getCenterLongitude() != null) {
            territory.setCenterLongitude(BigDecimal.valueOf(request.getCenterLongitude()));
        }
        territory.setRadiusKm(request.getRadiusKm());

        return TerritoryResponse.fromEntity(territoryRepository.save(territory));
    }

    @Override
    @Transactional
    public void deleteTerritory(UUID id) {
        Territory territory = territoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Territory", "id", id));
        territory.setActive(false);
        territoryRepository.save(territory);
    }
}
