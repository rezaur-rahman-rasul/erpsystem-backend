package com.hisabnikash.erp.identityaccess.integration.organization.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TenantProfileEventPayload(
        UUID id,
        String tenantCode,
        UUID legalEntityId,
        String companyName,
        boolean active
) {
}
