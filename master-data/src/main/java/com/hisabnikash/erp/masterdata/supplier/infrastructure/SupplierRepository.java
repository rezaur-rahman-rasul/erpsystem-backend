package com.hisabnikash.erp.masterdata.supplier.infrastructure;

import com.hisabnikash.erp.masterdata.supplier.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    boolean existsByLegalEntityIdAndCodeIgnoreCase(UUID legalEntityId, String code);

    boolean existsByLegalEntityIdAndCodeIgnoreCaseAndIdNot(UUID legalEntityId, String code, UUID id);
}
