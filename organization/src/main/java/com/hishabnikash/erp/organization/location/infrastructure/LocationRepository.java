package com.hishabnikash.erp.organization.location.infrastructure;

import com.hishabnikash.erp.organization.location.domain.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {

    boolean existsByCode(String code);

    Page<Location> findByLegalEntityId(UUID legalEntityId, Pageable pageable);

    Page<Location> findByBranchId(UUID branchId, Pageable pageable);

    List<Location> findByLegalEntityId(UUID legalEntityId);

    List<Location> findByBranchId(UUID branchId);
}
