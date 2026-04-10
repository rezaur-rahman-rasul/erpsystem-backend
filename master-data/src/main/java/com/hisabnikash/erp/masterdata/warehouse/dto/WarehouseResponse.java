package com.hisabnikash.erp.masterdata.warehouse.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record WarehouseResponse(
        UUID id,
        String tenantId,
        UUID legalEntityId,
        String code,
        String name,
        UUID branchId,
        String locationCode,
        boolean active,
        String createdBy,
        LocalDateTime createdAt,
        String lastUpdatedBy,
        LocalDateTime lastUpdatedAt
) {
}
