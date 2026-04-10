package com.hisabnikash.erp.enterprisestructure.integration.identity.infrastructure;

import com.hisabnikash.erp.enterprisestructure.integration.identity.domain.OrganizationAccessReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizationAccessReferenceRepository extends JpaRepository<OrganizationAccessReference, UUID> {

    boolean existsByBranchId(UUID branchId);
}
