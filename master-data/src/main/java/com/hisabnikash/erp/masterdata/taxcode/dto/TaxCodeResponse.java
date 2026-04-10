package com.hisabnikash.erp.masterdata.taxcode.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaxCodeResponse(
        UUID id,
        String tenantId,
        UUID legalEntityId,
        String code,
        String name,
        BigDecimal rate,
        boolean inclusive,
        boolean active,
        String createdBy,
        LocalDateTime createdAt,
        String lastUpdatedBy,
        LocalDateTime lastUpdatedAt
) {
}
