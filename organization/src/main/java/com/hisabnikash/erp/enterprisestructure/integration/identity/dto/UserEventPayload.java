package com.hisabnikash.erp.enterprisestructure.integration.identity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserEventPayload(
        UUID id,
        String username,
        String email,
        String displayName,
        String tenantId,
        String status
) {
}
