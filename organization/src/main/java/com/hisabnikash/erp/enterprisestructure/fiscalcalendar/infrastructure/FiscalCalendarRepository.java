package com.hisabnikash.erp.enterprisestructure.fiscalcalendar.infrastructure;

import com.hisabnikash.erp.enterprisestructure.fiscalcalendar.domain.FiscalCalendar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FiscalCalendarRepository extends JpaRepository<FiscalCalendar, UUID> {

    boolean existsByCode(String code);

    Page<FiscalCalendar> findByLegalEntityId(UUID legalEntityId, Pageable pageable);
}
