package com.hishabnikash.erp.organization.profitcenter.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ProfitCenterResponse {

    private UUID id;
    private UUID legalEntityId;
    private UUID businessUnitId;
    private String code;
    private String name;
    private String description;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
}
