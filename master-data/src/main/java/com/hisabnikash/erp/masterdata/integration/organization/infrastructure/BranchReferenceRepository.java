package com.hisabnikash.erp.masterdata.integration.organization.infrastructure;

import com.hisabnikash.erp.masterdata.integration.organization.domain.BranchReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BranchReferenceRepository extends JpaRepository<BranchReference, UUID> {

    List<BranchReference> findByLegalEntityIdAndActiveTrue(UUID legalEntityId);
}
