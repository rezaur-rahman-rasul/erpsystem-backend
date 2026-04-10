package com.hisabnikash.erp.masterdata.common.ownership;

import java.util.UUID;

public record BranchOwnership(
        String tenantId,
        UUID legalEntityId,
        UUID branchId
) {
}
