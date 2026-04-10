package com.hisabnikash.erp.masterdata.product.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String tenantId,
        UUID legalEntityId,
        String code,
        String name,
        String description,
        UUID unitOfMeasureId,
        boolean active,
        String createdBy,
        LocalDateTime createdAt,
        String lastUpdatedBy,
        LocalDateTime lastUpdatedAt
) {
}
