package com.hisabnikash.erp.masterdata.currency.infrastructure;

import com.hisabnikash.erp.masterdata.currency.domain.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CurrencyRepository extends JpaRepository<Currency, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);
}
