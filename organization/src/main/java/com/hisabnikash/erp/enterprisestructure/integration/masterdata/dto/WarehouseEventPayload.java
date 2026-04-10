package com.hisabnikash.erp.enterprisestructure.integration.masterdata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WarehouseEventPayload(
        UUID id,
        String code,
        String name,
        UUID branchId,
        String locationCode,
        boolean active
) {
}
