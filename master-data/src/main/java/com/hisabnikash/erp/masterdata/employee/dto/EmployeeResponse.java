package com.hisabnikash.erp.masterdata.employee.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String tenantId,
        UUID legalEntityId,
        String employeeNumber,
        String fullName,
        String email,
        String phone,
        String designation,
        boolean active,
        String createdBy,
        LocalDateTime createdAt,
        String lastUpdatedBy,
        LocalDateTime lastUpdatedAt
) {
}
