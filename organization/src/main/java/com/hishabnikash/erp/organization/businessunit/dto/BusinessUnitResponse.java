package com.hishabnikash.erp.organization.businessunit.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class BusinessUnitResponse {

    private UUID id;
    private UUID legalEntityId;
    private String code;
    private String name;
    private String description;
    private UUID managerEmployeeId;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
}
