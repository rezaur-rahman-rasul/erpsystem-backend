package com.hisabnikash.erp.masterdata.uom.infrastructure;

import com.hisabnikash.erp.masterdata.uom.domain.UnitOfMeasure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UnitOfMeasureRepository extends JpaRepository<UnitOfMeasure, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);
}
