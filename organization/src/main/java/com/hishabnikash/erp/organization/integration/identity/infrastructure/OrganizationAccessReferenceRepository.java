package com.hishabnikash.erp.organization.integration.identity.infrastructure;

import com.hishabnikash.erp.organization.integration.identity.domain.OrganizationAccessReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizationAccessReferenceRepository extends JpaRepository<OrganizationAccessReference, UUID> {

    boolean existsByBranchId(UUID branchId);
}
