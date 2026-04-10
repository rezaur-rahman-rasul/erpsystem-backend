package com.hisabnikash.erp.enterprisestructure.integration.masterdata.infrastructure;

import com.hisabnikash.erp.enterprisestructure.integration.masterdata.domain.WarehouseReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WarehouseReferenceRepository extends JpaRepository<WarehouseReference, UUID> {

    boolean existsByBranchIdAndActiveTrue(UUID branchId);
}
