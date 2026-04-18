package com.hishabnikash.erp.organization.department.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DepartmentResponse {

    private UUID id;
    private UUID legalEntityId;
    private UUID branchId;
    private UUID parentDepartmentId;
    private String code;
    private String name;
    private UUID headEmployeeId;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
}
