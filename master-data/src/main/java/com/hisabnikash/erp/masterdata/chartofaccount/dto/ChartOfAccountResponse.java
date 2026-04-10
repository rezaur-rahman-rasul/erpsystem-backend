package com.hisabnikash.erp.masterdata.chartofaccount.dto;

import com.hisabnikash.erp.masterdata.chartofaccount.domain.AccountType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChartOfAccountResponse(
        UUID id,
        String tenantId,
        UUID legalEntityId,
        String code,
        String name,
        AccountType accountType,
        UUID parentAccountId,
        boolean postingAllowed,
        boolean active,
        String createdBy,
        LocalDateTime createdAt,
        String lastUpdatedBy,
        LocalDateTime lastUpdatedAt
) {
}
