package com.hisabnikash.erp.enterprisestructure.integration.identity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationAccessEventPayload(
        UUID id,
        UUID userId,
        UUID legalEntityId,
        UUID branchId,
        boolean primaryAccess
) {
}
