package com.hisabnikash.erp.identityaccess.integration.organization.infrastructure;

import com.hisabnikash.erp.identityaccess.integration.organization.domain.LegalEntityReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LegalEntityReferenceRepository extends JpaRepository<LegalEntityReference, UUID> {
}
