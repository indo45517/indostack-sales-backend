package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.TerritoryRequest;
import com.billbharat.sales.dto.response.TerritoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TerritoryService {
    Page<TerritoryResponse> getTerritories(Pageable pageable);
    TerritoryResponse createTerritory(TerritoryRequest request);
    TerritoryResponse updateTerritory(UUID id, TerritoryRequest request);
    void deleteTerritory(UUID id);
}
