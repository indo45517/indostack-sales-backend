package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.TerritoryAssignRequest;
import com.billbharat.sales.dto.request.TerritoryBoundariesRequest;
import com.billbharat.sales.dto.request.TerritoryRemoveRequest;
import com.billbharat.sales.dto.request.TerritoryRequest;
import com.billbharat.sales.dto.response.AdminExecutiveResponse;
import com.billbharat.sales.dto.response.TerritoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TerritoryService {
    Page<TerritoryResponse> getTerritories(Pageable pageable);
    TerritoryResponse getTerritoryById(UUID id);
    TerritoryResponse createTerritory(TerritoryRequest request);
    TerritoryResponse updateTerritory(UUID id, TerritoryRequest request);
    void deleteTerritory(UUID id);
    Map<String, Object> updateBoundaries(UUID territoryId, TerritoryBoundariesRequest request);
    Map<String, Object> assignExecutive(UUID territoryId, TerritoryAssignRequest request);
    List<AdminExecutiveResponse> getTerritoryExecutives(UUID territoryId);
    void removeExecutive(UUID territoryId, TerritoryRemoveRequest request);
}
