package com.hisabnikash.erp.masterdata.warehouse.infrastructure;

import com.hisabnikash.erp.masterdata.warehouse.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    boolean existsByLegalEntityIdAndCodeIgnoreCase(UUID legalEntityId, String code);

    boolean existsByLegalEntityIdAndCodeIgnoreCaseAndIdNot(UUID legalEntityId, String code, UUID id);

    List<Warehouse> findByBranchIdIn(Collection<UUID> branchIds);
}
