package com.hisabnikash.erp.enterprisestructure.profitcenter.infrastructure;

import com.hisabnikash.erp.enterprisestructure.profitcenter.domain.ProfitCenter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfitCenterRepository extends JpaRepository<ProfitCenter, UUID> {

    boolean existsByCode(String code);

    Page<ProfitCenter> findByLegalEntityId(UUID legalEntityId, Pageable pageable);
}
