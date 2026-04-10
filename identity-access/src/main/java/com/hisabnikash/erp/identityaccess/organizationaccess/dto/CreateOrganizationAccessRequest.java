package com.hisabnikash.erp.identityaccess.organizationaccess.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateOrganizationAccessRequest(
        @NotNull UUID legalEntityId,
        UUID branchId,
        boolean primaryAccess
) {
}
