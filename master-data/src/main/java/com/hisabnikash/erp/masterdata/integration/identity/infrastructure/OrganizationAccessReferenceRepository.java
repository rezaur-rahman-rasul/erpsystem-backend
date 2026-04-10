package com.hisabnikash.erp.masterdata.integration.identity.infrastructure;

import com.hisabnikash.erp.masterdata.integration.identity.domain.OrganizationAccessReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizationAccessReferenceRepository extends JpaRepository<OrganizationAccessReference, UUID> {

    List<OrganizationAccessReference> findByUserId(UUID userId);
}
