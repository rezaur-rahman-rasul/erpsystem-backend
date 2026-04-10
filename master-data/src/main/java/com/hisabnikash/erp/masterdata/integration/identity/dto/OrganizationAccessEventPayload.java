package com.hisabnikash.erp.masterdata.integration.identity.dto;

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
