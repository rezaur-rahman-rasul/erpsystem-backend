package com.hisabnikash.erp.masterdata.integration.organization.infrastructure;

import com.hisabnikash.erp.masterdata.integration.organization.domain.LegalEntityReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LegalEntityReferenceRepository extends JpaRepository<LegalEntityReference, UUID> {
}
