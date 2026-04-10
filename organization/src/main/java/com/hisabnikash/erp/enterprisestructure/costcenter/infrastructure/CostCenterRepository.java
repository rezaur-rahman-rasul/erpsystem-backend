package com.hisabnikash.erp.enterprisestructure.costcenter.infrastructure;

import com.hisabnikash.erp.enterprisestructure.costcenter.domain.CostCenter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CostCenterRepository extends JpaRepository<CostCenter, UUID> {

    boolean existsByCode(String code);

    Page<CostCenter> findByLegalEntityId(UUID legalEntityId, Pageable pageable);
}
