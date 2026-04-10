package com.hisabnikash.erp.masterdata.chartofaccount.infrastructure;

import com.hisabnikash.erp.masterdata.chartofaccount.domain.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, UUID> {

    boolean existsByLegalEntityIdAndCodeIgnoreCase(UUID legalEntityId, String code);

    boolean existsByLegalEntityIdAndCodeIgnoreCaseAndIdNot(UUID legalEntityId, String code, UUID id);
}
