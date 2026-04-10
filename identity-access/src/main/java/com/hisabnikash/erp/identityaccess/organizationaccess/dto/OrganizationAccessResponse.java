package com.hisabnikash.erp.identityaccess.organizationaccess.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrganizationAccessResponse(
        UUID id,
        UUID userId,
        UUID legalEntityId,
        UUID branchId,
        boolean primaryAccess,
        String createdBy,
        LocalDateTime createdAt,
        String lastUpdatedBy,
        LocalDateTime lastUpdatedAt
) {
}
