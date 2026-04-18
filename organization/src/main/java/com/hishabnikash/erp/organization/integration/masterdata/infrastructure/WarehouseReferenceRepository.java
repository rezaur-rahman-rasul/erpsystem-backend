package com.hishabnikash.erp.organization.integration.masterdata.infrastructure;

import com.hishabnikash.erp.organization.integration.masterdata.domain.WarehouseReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WarehouseReferenceRepository extends JpaRepository<WarehouseReference, UUID> {

    boolean existsByBranchIdAndActiveTrue(UUID branchId);
}
