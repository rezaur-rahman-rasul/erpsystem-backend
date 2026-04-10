package com.hisabnikash.erp.identityaccess.integration.organization.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BranchEventPayload(
        UUID id,
        UUID legalEntityId,
        String code,
        String name,
        String status
) {
}
