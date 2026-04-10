package com.hisabnikash.erp.masterdata.paymentterm.infrastructure;

import com.hisabnikash.erp.masterdata.paymentterm.domain.PaymentTerm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentTermRepository extends JpaRepository<PaymentTerm, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, UUID id);
}
