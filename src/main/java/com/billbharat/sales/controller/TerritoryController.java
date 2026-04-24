package com.billbharat.sales.controller;

import com.billbharat.sales.dto.request.TerritoryRequest;
import com.billbharat.sales.service.TerritoryService;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales/territories")
@RequiredArgsConstructor
@Tag(name = "Territories", description = "Territory management")
@SecurityRequirement(name = "bearerAuth")
public class TerritoryController {

    private final TerritoryService territoryService;

    @GetMapping
    @Operation(summary = "Get all territories")
    public ResponseEntity<Map<String, Object>> getTerritories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = territoryService.getTerritories(PageRequest.of(page, size, Sort.by("name").ascending()));
        return ResponseEntity.ok(ResponseUtil.paginated(result.getContent(), result.getTotalElements(), page + 1, size));
    }

    @PostMapping
    @Operation(summary = "Create a new territory")
    public ResponseEntity<Map<String, Object>> createTerritory(@Valid @RequestBody TerritoryRequest request) {
        var response = territoryService.createTerritory(request);
        return ResponseEntity.ok(ResponseUtil.success("Territory created", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a territory")
    public ResponseEntity<Map<String, Object>> updateTerritory(
            @PathVariable UUID id,
            @Valid @RequestBody TerritoryRequest request) {
        var response = territoryService.updateTerritory(id, request);
        return ResponseEntity.ok(ResponseUtil.success("Territory updated", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a territory")
    public ResponseEntity<Map<String, Object>> deleteTerritory(@PathVariable UUID id) {
        territoryService.deleteTerritory(id);
        return ResponseEntity.ok(ResponseUtil.success("Territory deleted", null));
    }
}
