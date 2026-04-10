package com.hisabnikash.erp.identityaccess.integration.organization.infrastructure;

import com.hisabnikash.erp.identityaccess.integration.organization.domain.BranchReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BranchReferenceRepository extends JpaRepository<BranchReference, UUID> {
}
