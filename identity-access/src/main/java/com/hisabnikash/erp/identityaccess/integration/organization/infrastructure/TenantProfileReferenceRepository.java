package com.hisabnikash.erp.identityaccess.integration.organization.infrastructure;

import com.hisabnikash.erp.identityaccess.integration.organization.domain.TenantProfileReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TenantProfileReferenceRepository extends JpaRepository<TenantProfileReference, UUID> {

    boolean existsByTenantCodeIgnoreCaseAndActiveTrue(String tenantCode);
}
