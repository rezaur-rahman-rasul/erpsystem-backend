package com.hisabnikash.erp.masterdata.customer.infrastructure;

import com.hisabnikash.erp.masterdata.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByLegalEntityIdAndCodeIgnoreCase(UUID legalEntityId, String code);

    boolean existsByLegalEntityIdAndCodeIgnoreCaseAndIdNot(UUID legalEntityId, String code, UUID id);
}
