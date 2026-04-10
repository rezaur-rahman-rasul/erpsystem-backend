package com.hisabnikash.erp.masterdata.integration.organization.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LegalEntityEventPayload(
        UUID id,
        String code,
        String legalName,
        String status
) {
}
