package com.hisabnikash.erp.enterprisestructure.businessunit.infrastructure;

import com.hisabnikash.erp.enterprisestructure.businessunit.domain.BusinessUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BusinessUnitRepository extends JpaRepository<BusinessUnit, UUID> {

    boolean existsByCode(String code);

    Page<BusinessUnit> findByLegalEntityId(UUID legalEntityId, Pageable pageable);

    List<BusinessUnit> findByLegalEntityId(UUID legalEntityId);
}
