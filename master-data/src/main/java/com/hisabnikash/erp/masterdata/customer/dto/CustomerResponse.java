package com.hisabnikash.erp.masterdata.customer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String tenantId,
        UUID legalEntityId,
        String code,
        String name,
        String email,
        String phone,
        String taxNumber,
        boolean active,
        String createdBy,
        LocalDateTime createdAt,
        String lastUpdatedBy,
        LocalDateTime lastUpdatedAt
) {
}
