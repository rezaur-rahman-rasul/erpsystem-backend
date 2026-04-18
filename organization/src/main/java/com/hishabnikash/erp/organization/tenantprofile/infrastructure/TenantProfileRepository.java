package com.hishabnikash.erp.organization.tenantprofile.infrastructure;

import com.hishabnikash.erp.organization.tenantprofile.domain.TenantProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TenantProfileRepository extends JpaRepository<TenantProfile, UUID> {

    boolean existsByTenantCodeIgnoreCase(String tenantCode);

    Page<TenantProfile> findByLegalEntityId(UUID legalEntityId, Pageable pageable);
}
