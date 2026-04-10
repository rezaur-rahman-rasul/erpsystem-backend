package com.hisabnikash.erp.masterdata.uom.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UnitOfMeasureResponse(
        UUID id,
        String code,
        String name,
        String category,
        boolean baseUnit,
        BigDecimal conversionFactor,
        boolean active,
        String createdBy,
        LocalDateTime createdAt,
        String lastUpdatedBy,
        LocalDateTime lastUpdatedAt
) {
}
