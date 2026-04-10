package com.hisabnikash.erp.identityaccess.user.dto;

import com.hisabnikash.erp.identityaccess.organizationaccess.dto.OrganizationAccessResponse;
import com.hisabnikash.erp.identityaccess.user.domain.UserStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String displayName,
        String tenantId,
        UserStatus status,
        String createdBy,
        LocalDateTime createdAt,
        String lastUpdatedBy,
        LocalDateTime lastUpdatedAt,
        Set<RoleSummaryResponse> roles,
        Set<OrganizationAccessResponse> organizationAccesses
) {
}
