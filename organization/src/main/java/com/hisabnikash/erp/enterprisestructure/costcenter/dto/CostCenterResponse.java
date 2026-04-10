package com.hisabnikash.erp.enterprisestructure.costcenter.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CostCenterResponse {

    private UUID id;
    private UUID legalEntityId;
    private UUID departmentId;
    private String code;
    private String name;
    private String description;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
}
