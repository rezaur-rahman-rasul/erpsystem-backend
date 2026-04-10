package com.hisabnikash.erp.masterdata.taxcode.infrastructure;

import com.hisabnikash.erp.masterdata.taxcode.domain.TaxCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaxCodeRepository extends JpaRepository<TaxCode, UUID> {

    boolean existsByLegalEntityIdAndCodeIgnoreCase(UUID legalEntityId, String code);

    boolean existsByLegalEntityIdAndCodeIgnoreCaseAndIdNot(UUID legalEntityId, String code, UUID id);
}
