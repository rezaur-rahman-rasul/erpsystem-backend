package com.hishabnikash.erp.organization.subsidiary.infrastructure;

import com.hishabnikash.erp.organization.subsidiary.domain.Subsidiary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubsidiaryRepository extends JpaRepository<Subsidiary, UUID> {

    boolean existsByCode(String code);

    Page<Subsidiary> findByParentLegalEntityId(UUID parentLegalEntityId, Pageable pageable);
}
